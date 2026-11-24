package br.com.ifba.prg03projetosgcr.cliente.dto;

import br.com.ifba.prg03projetosgcr.cliente.entity.Endereco;

/**
 * Dados de saída do endereço de um cliente.
 *
 * @author João Luis
 */
public record EnderecoResponseDTO(
        Long id,
        String bairro,
        String rua,
        String numero,
        String pontoReferencia
) {
    public static EnderecoResponseDTO fromEntity(Endereco endereco) {
        if (endereco == null) {
            return null;
        }
        return new EnderecoResponseDTO(
                endereco.getId(),
                endereco.getBairro(),
                endereco.getRua(),
                endereco.getNumero(),
                endereco.getPontoReferencia()
        );
    }
}
