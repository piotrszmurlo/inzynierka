from fastapi import FastAPI, HTTPException
import python_example as p
from fastapi.middleware.cors import CORSMiddleware

app = FastAPI()

origins = [
    "localhost:3000",
    "http://localhost:3000",
]

app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

@app.get("/")
async def root():
    # raise HTTPException(status_code=404, detail="Item not found")
    return {"message": f"Hello {p.subtract(100, 33)}"}



@app.get("/data")
async def get_example_data():
    return {"data": [4, 3, 2, 1]}
