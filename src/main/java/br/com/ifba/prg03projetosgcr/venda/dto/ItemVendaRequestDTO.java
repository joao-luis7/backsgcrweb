package br.com.ifba.prg03projetosgcr.venda.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO de entrada para item de venda.
 * Preço unitário e subtotal são calculados exclusivamente no servidor (SEC-03).
 *
 * @author João Luis
 */
public record ItemVendaRequestDTO(
        @NotNull(message = "O ID do produto é obrigatório.")
        Long produtoId,

        @Positive(message = "A quantidade deve ser maior que zero.")
        int quantidade
) {
}
