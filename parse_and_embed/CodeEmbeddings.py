import os
import csv 
import shutil
import openai
import pandas as pd
import numpy as np
from transformers import GPT2TokenizerFast
from dotenv import load_dotenv
import time
from CodeParser import CodeParser  # Importing the CodeParser class


load_dotenv()
REPOSITORY_PATH = os.environ[ 'REPOSITORY_PATH' ]

class CodeEmbeddings:
    def __init__(self, workspace_path: str, keyArg: str ):
        self.workspace_path = workspace_path
        openai.api_key = keyArg  #os.getenv("OPENAI_API_KEY", "")

        self.DOC_EMBEDDINGS_MODEL = f"text-embedding-ada-002"
        self.QUERY_EMBEDDINGS_MODEL = f"text-embedding-ada-002"

        self.SEPARATOR = "\n* "

        self.tokenizer = GPT2TokenizerFast.from_pretrained("gpt2")
        self.separator_len = len(self.tokenizer.tokenize(self.SEPARATOR))

        self.parser = CodeParser( "cpp" )  # Instantiate the CodeParser class

    def compute_repository_embeddings(self):
        try:
            playground_data_path = os.path.join(self.workspace_path, 'playground_data')
            for filename in os.listdir(playground_data_path):
                file_path = os.path.join(playground_data_path, filename)
                try:
                    if os.path.isfile(file_path) or os.path.islink(file_path):
                        os.unlink(file_path)
                    elif os.path.isdir(file_path):
                        shutil.rmtree(file_path)
                except Exception as e:
                    print(f"Failed to delete {file_path}. Reason: {str(e)}")
        except Exception as e:
            print(f"Error: {str(e)}")

        info = self.extract_info( self.workspace_path + "/" + REPOSITORY_PATH )
        self.save_info_to_csv(info)

        df = pd.read_csv(os.path.join(self.workspace_path, 'playground_data/repository_info.csv'))
        df = df.set_index(["filePath", "lineCoverage"])
        self.df = df
        context_embeddings = self.compute_doc_embeddings(df)
        self.save_doc_embeddings_to_csv(context_embeddings, df, os.path.join(self.workspace_path, 'playground_data/doc_embeddings.csv'))

        try:
            self.document_embeddings = self.load_embeddings(os.path.join(self.workspace_path, 'playground_data/doc_embeddings.csv'))
        except:
            pass

    def extract_info(self, directory_path):
        cpp_files = [os.path.join(root, name)
                    for root, dirs, files in os.walk(directory_path)
                    for name in files
                    if name.endswith((".hpp", ".cpp")) and not "googletest" in root]

        parsed_nodes = []
        for cpp_file in cpp_files:
            parsed_nodes.extend(self.parser.parse_file(cpp_file))

        return [(file_path, str(line_coverage), self._extract_node_content(node)) for file_path, line_coverage, node in parsed_nodes]

    def _extract_node_content(self, node):
        if isinstance(node, str):
            return node
        else:
            return ''.join(self._extract_node_content(child) for child in node.children)


    def save_info_to_csv(self, info):
        count = 0
        os.makedirs(os.path.join(self.workspace_path, "playground_data"), exist_ok=True)
        with open(os.path.join(self.workspace_path, 'playground_data/repository_info.csv'), "w", newline="") as csvfile:
            writer = csv.writer(csvfile)
            writer.writerow(["filePath", "lineCoverage", "content"])
            for file_path, line_coverage, content in info:
                count = count + 1
                print ( count )
                writer.writerow([file_path, line_coverage, content])

    def get_relevant_code_chunks(self, task_description: str, task_context: str):
        query = task_description + "\n" + task_context
        most_relevant_document_sections = self.order_document_sections_by_query_similarity(query, self.document_embeddings)
        selected_chunks = []
        for _, section_index in most_relevant_document_sections:
            try:
                document_section = self.df.loc[section_index]
                selected_chunks.append(self.SEPARATOR + document_section['content'].replace("\n", " "))
                if len(selected_chunks) >= 2:
                    break
            except:
                pass

        return selected_chunks

    def get_embedding(self, text: str, model: str) -> list[float]:
        result = openai.Embedding.create(
        model=model,
        input=text
        )
        return result["data" ][ 0 ][ "embedding"]

    def get_doc_embedding(self, text: str) -> list[float]:
        return self.get_embedding(text, self.DOC_EMBEDDINGS_MODEL)

    def get_query_embedding(self, text: str) -> list[float]:
        return self.get_embedding(text, self.QUERY_EMBEDDINGS_MODEL)

    def compute_doc_embeddings(self, df: pd.DataFrame) -> dict[tuple[str, str], list[float]]:
        embeddings = {}
        count = 0
        for idx, r in df.iterrows():
            count = count + 1
            print ( count )
            print ( r )
            time.sleep( .1 )
            embeddings[idx] = self.get_doc_embedding(r.content.replace("\n", " "))
        return embeddings

    def save_doc_embeddings_to_csv(self, doc_embeddings: dict, df: pd.DataFrame, csv_filepath: str):
        EMBEDDING_DIM = len(list(doc_embeddings.values())[0])
        embeddings_df = pd.DataFrame(columns=["filePath", "lineCoverage"] + [f"{i}" for i in range(EMBEDDING_DIM)])
        count = 0
        for idx, _ in df.iterrows():
            print ( len( embeddings_df ))
            embedding = doc_embeddings[idx]
            row = [idx[ 0 ], idx[ 1 ]] + embedding
            embeddings_df.loc[ len( embeddings_df )] = row
        embeddings_df.to_csv( csv_filepath, index=False )

    def vector_similarity(self, x: list[float], y: list[float]) -> float:
        return np.dot(np.array(x), np.array(y))

    def order_document_sections_by_query_similarity(self, query: str, contexts: dict[(str, str), np.array]) -> list[(float, (str, str))]:
        query_embedding = self.get_query_embedding(query)
        document_similarities = sorted([
            (self.vector_similarity(query_embedding, doc_embedding), doc_index) for doc_index, doc_embedding in contexts.items()
        ], reverse=True)
        
        return document_similarities

    def load_embeddings(self, fname: str) -> dict[tuple[str, str], list[float]]:       
        df = pd.read_csv(fname, header=0)
        max_dim = max([int(c) for c in df.columns if c != "filePath" and c != "lineCoverage"])
        return {
            (r.filePath, r.lineCoverage): [r[str(i)] for i in range(max_dim + 1)] for _, r in df.iterrows()
        }
