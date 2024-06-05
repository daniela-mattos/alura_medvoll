package med.voll.api.controller;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import med.voll.api.medico.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medicos")
public class MedicoController {

    @Autowired
    private MedicoRepository repository;

    @PostMapping
    @Transactional
    public void cadastrar(@RequestBody @Valid DadosCadastroMedico dados) {
        repository.save(new Medico(dados));
    }

    @GetMapping
    public Page<DadosListaMedico> listar(@PageableDefault(size = 10, sort = {"nome"}) Pageable pageable) {
        //Page gera os dados de paginação para o front-end, usado junto com o Pageable,
        //cuidar ao selecionar, para pegar o do spring
        return repository.findAllByAtivoTrue(pageable)
                .map(DadosListaMedico::new);

        //esse método já tem um "stream" e gera um "tolist", itens que seriam adicionados
        //caso devolvesse um "List" com uso do findAll + stream
    }
    //@PageableDefault é uma forma de mandar os dados já com alguns parâmetros
    //ele é sobre escrito se a url vier do front com parâmetros
    //exemplo: http://localhost:8080/medicos?sort=crm,desc&size=2


    @PutMapping
    @Transactional
    public void atualizar(@RequestBody @Valid DadosAtualizaMedico dados) {
        var medico = repository.getReferenceById(dados.id());
        medico.atualizarInformacoes(dados);
    }

    //deleta diretamente no banco
//    @DeleteMapping("/{id}")
//    @Transactional
//    public void excluir(@PathVariable Long id) {
//        repository.deleteById(id);
//    }


    //dessa forma ele marca o item como "inativo" em vez de remover o
    //item do banco
    @DeleteMapping("/{id}")
    @Transactional
    public void excluir(@PathVariable Long id) {
        var medico = repository.getReferenceById(id);
        medico.excluir();
    }
}



