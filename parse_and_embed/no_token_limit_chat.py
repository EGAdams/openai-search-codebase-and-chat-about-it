import os, sys
import openai
from dotenv import load_dotenv
from CodeEmbeddings import CodeEmbeddings  # Import the Embeddings class
import numpy as np

RESULTS_CHAT_WITH = 200
MAX_TOKENS = 15000 # 6500  # 8191
MAX_COMBINED_RESULTS_LENGTH = 15000 # 6000
CACHED_EMBEDDINGS_PATH = "code_search_openai.json"

load_dotenv()

openai.api_key = os.environ['OPENAI_API_KEY']
# workspace_path = os.environ['WORKSPACE_PATH']
# openai.api_key = "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" leaked key!

# Instantiate the Embeddings class
workspace_path = os.path.dirname(os.path.realpath(__file__))
embeddings = CodeEmbeddings( workspace_path, openai.api_key )


# print("Type in your query/prompt:")
# code_query = input()
code_query = "Please explain in great detail what the ScoreBoard and SetDrawer objects do."

# Compute the embeddings for the repository
embeddings.compute_repository_embeddings()

# Generate or load all embeddings
print("Generated or loaded all embeddings")

query_embedding = embeddings.get_embedding( code_query, 'text-embedding-ada-002' )
if not query_embedding:
    print( "Error: query embedding is empty ")
    sys.exit( 1 )

# Define the search function using the Embeddings class
def search_functions(df, number_similar_results=200, n_lines=1000):
    # Reassemble the embeddings from the individual columns
    embedding_columns = [col for col in df.columns if col not in ["filePath", "lineCoverage", "content"]]
    df["code_embedding"] = df[embedding_columns].values.tolist()
    # df['similarities'] = df.code_embedding.apply( lambda x: embeddings.vector_similarity(x, query_embedding))
    df['similarities'] = df.code_embedding.apply(
    lambda x: embeddings.vector_similarity(x, query_embedding) if len(x) > 0 else np.nan)
    
    res = df.sort_values('similarities', ascending=False).head(
        number_similar_results)

    combined_results = ""
    srs = []
    for r in res.iterrows():
        code = "\n".join(r[1].content.split("\n")[:n_lines])
        # srs.append([r[1].filepath+":"+r[1].node_type, r[1].similarities, code])
        srs.append([r[0][0]+":"+r[0][1], r[1].similarities, code])
        print(r[0][0] + ":" + r[0][1] + "  score=" + str(round(r[1].similarities, 3)))
        print(code)
        print('-'*70)
        combined_results += code + "\n\n\n"
    srs.sort(key=lambda x: x[1], reverse=True)
    print("\n\n\n\n**************************************************************\n")
    print("Best ranking code snippet:")
    print(srs[0][2])
    # return res
    return combined_results[:MAX_COMBINED_RESULTS_LENGTH]

print("Running search functions to find similar code")
related_code = search_functions(embeddings.df, number_similar_results=200) # related_code is a just a bunch of coords
header = """Answer the question using the provided context and any other available information."\n\nContext:\n"""
final_prompt = header + \
    "".join(related_code) + "\n\n Q: " + code_query + "\n A:"
print ( "final_prompt:" )
print ( final_prompt )
print ( "creating final answer..." )
final_answer = openai.ChatCompletion.create(
    messages=[{"role": "user", "content": final_prompt}],
    model="gpt-3.5-turbo"   # model="gpt-4-32k-0613"
)
 
print('-'*70)
print('-'*70)
print("\n\n\nChatGPT says:\n\n", final_answer.choices[0].message.content)
