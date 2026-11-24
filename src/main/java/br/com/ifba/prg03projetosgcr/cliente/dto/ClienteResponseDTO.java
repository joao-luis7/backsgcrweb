package br.com.ifba.prg03projetosgcr.cliente.dto;

import br.com.ifba.prg03projetosgcr.cliente.entity.Cliente;

/**
 * Dados de saída de um cliente, incluindo id, saldoDevedor e endereço.
 *
 * @author João Luis
 */
public record ClienteResponseDTO(
        Long id,
        String nome,
        String telefone,
        boolean ativo,
        double saldoDevedor,
        EnderecoResponseDTO endereco
) {
    public static ClienteResponseDTO fromEntity(Cliente cliente) {
        if (cliente == null) {
            return null;
        }
        return new ClienteResponseDTO(
                cliente.getId(),
                cliente.getNome(),
                cliente.getTelefone(),
                cliente.isAtivo(),
                cliente.getSaldoDevedor(),
                EnderecoResponseDTO.fromEntity(cliente.getEndereco())
        );
    }
}
