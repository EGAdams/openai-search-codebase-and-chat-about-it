import ast
import os

class CodebaseEmbeddings(Embeddings):
    def __init__(self, workspace_path: str):
        super().__init__(workspace_path)

    def extract_info(self, REPOSITORY_PATH):
        info = []

        # Iterate through the files in the repository
        for root, dirs, files in os.walk(REPOSITORY_PATH):
            for file in files:
                file_path = os.path.join(root, file)
                
                # Only process .py files
                if not file_path.endswith('.py'):
                    continue

                # Read the contents of the file
                with open(file_path, "r", encoding="utf-8") as f:
                    try:
                        contents = f.read()
                    except:
                        continue

                # Parse the code into an AST
                tree = ast.parse(contents)

                # Extract classes and functions
                for node in ast.walk(tree):
                    if isinstance(node, (ast.ClassDef, ast.FunctionDef)):
                        # Get the source lines for the class or function
                        start_line = node.lineno
                        end_line = start_line + len(node.body)
                        source_lines = contents.split('\n')[start_line-1:end_line]

                        # Join the source lines back into a single string
                        source_code = '\n'.join(source_lines)

                        # Add the file path, line coverage, and source code to the list
                        line_coverage = (start_line, end_line)
                        info.append((os.path.join(root, file), line_coverage, source_code))

        return info
