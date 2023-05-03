from fastapi import FastAPI
import python_example as p
app = FastAPI()


@app.get("/")
async def root():
    return {"message": f"Hello {p.subtract(100, 33)}"}



@app.get("/hello/{number}")
async def say_hello(number: int):
    return {"message": f"Hello {p.subtract(100, number)}"}
