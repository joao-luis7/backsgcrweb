package br.com.ifba.prg03projetosgcr.cliente.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Dados de entrada do endereço vinculado a um cliente.
 *
 * @author João Luis
 */
public record EnderecoRequestDTO(
        @NotBlank(message = "O bairro é obrigatório.")
        String bairro,

        @NotBlank(message = "A rua é obrigatória.")
        String rua,

        @NotBlank(message = "O número é obrigatório.")
        String numero,

        String pontoReferencia
) {
}
