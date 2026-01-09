# SOFTWARE ENGINEERING LAB PROJECT

This repository contains the source code of a Software Engineering laboratory project,  
an e-commerce platform for chairs.

## How to Run

### Requirements

<li>
    Java 21
</li>
<li>
    Docker
</li>
<li>
    Maven
</li>

### Test Environments

Run the `run-tests.sh` command to execute all automated tests of the application.

To run tests individually, you must execute the command  
`docker compose -f docker-compose-test.yml up --build` once before running each test separately.

### Development Environment

Run the command `docker compose -f docker-compose.yaml` to start the containers and create the base structure to use the application.  
In the `seed.sql` file, you can find user credentials and the initial database data.

#### Administrator

<li>Email: admin@exemplo.com</li>
<li>Password: Admin1234!</li>

#### Customer

<li>
Email: cliente@exemplo.com
</li>
<li>
Password: Abcd1234!
</li>

#### Sales Manager

<li>
Email: vendas@exemplo.com
</li>
<li>
Password: Sales1234!
</li>

Notes:
<li>
    The database used is PostgreSQL.
</li>
<li>
    The chatbot model may be unavailable at times, as the application fetches the models 
    `nomic-embed-text:v1.5` and `gemma-3b` (it was supposed to be Deepseek, but due to memory
    capacity constraints, this model was chosen instead) from Ollama. If the download fails,
    the models can be downloaded manually inside the Ollama container.
</li>
<li>
    If the mentioned models are not available, they will be fetched from the Ollama registry,
    which may cause a delay during the application's first startup.
</li>
