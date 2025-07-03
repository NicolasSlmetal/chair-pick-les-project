# PROJETO DE LABORATÓRIO DE ENGENHARIA DE SOFTWARE

Este repositório contém o código fonte do projeto de laboratório de Engenharia de Software, 
um e-commerce de cadeiras. 

## Como rodar

### Requisitos

<li>
    Java 21
</li>
<li>
    Docker
</li>
<li>
    Maven
</li>

### Ambientes de testes

Execute o comando `run-tests.sh` para rodar todos os testes automatizados da aplicação.

Para executar cada teste individualmente, deve-se executar o comando `docker compose -f docker-compose-test.yml up --build` uma vez antes de executar individualmente.

### Ambientes de desenvolvimento

Execute o comando `docker compose -f docker-compose.yaml` para iniciar os containers e criar a estrutura base para usar a aplicação. No arquivo `seed.sql`, pode-se encontrar os logins dos usuários e os dados iniciais do banco de dados.

#### Administrador

<li>Email: admin@exemplo.com</li>
<li>Senha: Admin1234!</li>

#### Cliente
<li>
Email: cliente@exemplo.com
</li>
<li>
Senha: Abcd1234!
</li>

#### Gerente de vendas

<li>
Email: vendas@exemplo.com
</li>
<li>
Senha: Sales1234!
</li>

Observações:
<li>
    O banco de dados utilizado é o PostgreSQL.
</li>
<li>
O modelo de chatbot pode ficar indisponível em alguns momentos, pois a aplicação busca os modelos ´nomic-embed-text:v1.5´ e ´gemma-3b (Era para ser Deepseek, no entanto, por questões de capacidade de memória, foi decidido utilizar esse modelo)´ no Ollama. Caso não consiga baixar, é possível baixar os modelos manualmente no container ollama.
</li>
<li>
Se os modelos citados não estiverem disponíveis, eles serão buscados na base da Ollama e isso pode causar um atraso na inicialização da aplicação pela primeira vez.
</li>
