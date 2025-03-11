
const errorMessageMap = new Map();

errorMessageMap.set("INVALID_CPF", "CPF inválido");
errorMessageMap.set("INVALID_PHONE_DDD", "DDD de telefone inválido");
errorMessageMap.set("INVALID_PHONE", "Telefone inválido");
errorMessageMap.set("INVALID_BORN_DATE", "Data de nascimento inválida");
errorMessageMap.set("INVALID_PHONE_TYPE", "Tipo de telefone inválido");
errorMessageMap.set("INVALID_STREET", "Rua inválida");
errorMessageMap.set("INVALID_ADDRESS_NUMBER", "Número do endereço inválido");
errorMessageMap.set("INVALID_NEIGHBORHOOD", "Bairro inválido");
errorMessageMap.set("INVALID_CITY", "Cidade inválida");
errorMessageMap.set("INVALID_STATE", "Estado inválido");
errorMessageMap.set("INVALID_COUNTRY", "País inválido");
errorMessageMap.set("INVALID_CEP", "CEP inválido");
errorMessageMap.set("INVALID_ADDRESS_TYPE", "Tipo de endereço inválido");
errorMessageMap.set("INVALID_CUSTOMER_NAME", "Nome do cliente inválido");
errorMessageMap.set("INVALID_ADDRESS_NAME", "Nome do endereço inválido");
errorMessageMap.set("INVALID_ADDRESS_TYPES", "Tipos de endereço inválidos");
errorMessageMap.set("ADDRESSES_REQUIRED", "Endereços são obrigatórios");
errorMessageMap.set("CREDIT_CARDS_REQUIRED", "Cartões de crédito são obrigatórios");
errorMessageMap.set("REQUIRED_USER", "Usuário é obrigatório");
errorMessageMap.set("GENRE_REQUIRED", "Gênero é obrigatório");
errorMessageMap.set("INVALID_CREDIT_CARD_NUMBER", "Número do cartão de crédito inválido");
errorMessageMap.set("INVALID_CVV", "CVV inválido");
errorMessageMap.set("INVALID_CARD_BRAND", "Bandeira do cartão inválida");
errorMessageMap.set("INVALID_CARD_HOLDER", "Nome do titular do cartão inválido");
errorMessageMap.set("ADDRESS_DEFAULT_REQUIRED", "Endereço padrão é obrigatório");
errorMessageMap.set("INVALID_PASSWORD", "Senha inválida");
errorMessageMap.set("INVALID_EMAIL", "E-mail inválido");


export function parseErrorMessages(errorCodes) {
  return errorCodes ? errorCodes.split("\n").map(code => {

    return errorMessageMap.get(code) || "Erro desconhecido";
  }) : "Erro desconhecido";
}
