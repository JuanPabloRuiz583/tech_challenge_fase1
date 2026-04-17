package br.com.fiap.Gestao.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.Map;

/**
 * Implementação do padrão RFC 7807 - Problem Details for HTTP APIs
 * 
 * Este DTO segue a RFC 7807 para padronizar respostas de erro em APIs REST.
 * 
 * @see <a href="https://tools.ietf.org/html/rfc7807">RFC 7807</a>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProblemDetailDTO(
        /**
         * URI que identifica o tipo do problema.
         * Exemplo: "https://api.exemplo.com/errors/usuario-duplicado"
         */
        String type,

        /**
         * Título curto legível por humanos do tipo de problema.
         * Não deve variar com a instância do problema.
         * Exemplo: "Usuário Duplicado"
         */
        String title,

        /**
         * Código de status HTTP da resposta.
         * Deve ser consistente com o código enviado.
         */
        Integer status,

        /**
         * Descrição legível por humanos do problema específico.
         * Pode variar com cada instância do problema.
         */
        String detail,

        /**
         * URI que identifica especificamente a instância do problema.
         * Geralmente é o caminho da requisição.
         */
        String instance,

        /**
         * Timestamp de quando o erro ocorreu.
         */
        Instant timestamp,

        /**
         * Mapa de erros de validação de campos.
         * Chave: nome do campo
         * Valor: mensagem de erro
         */
        Map<String, String> fieldErrors
) {
}

