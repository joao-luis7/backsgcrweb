/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.ifba.prg03projetosgcr.funcionario.service;

import br.com.ifba.prg03projetosgcr.funcionario.entity.Funcionario;
import java.util.List;

/**
 *
 * @author joaol
 */
public interface FuncionarioService {
    Funcionario autenticar(String username, String senha);

    void save(Funcionario funcionario);

    List<Funcionario> findAll();

    List<Funcionario> findAllAtivos();

    Funcionario findById(Long id);

    Funcionario update(Funcionario funcionario);
}
