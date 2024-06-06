package med.voll.api.controller;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import med.voll.api.domain.medico.DadosListaMedico;
import med.voll.api.domain.medico.Medico;
import med.voll.api.domain.medico.MedicoRepository;
import med.voll.api.domain.medico.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/medicos")
public class MedicoController {

    @Autowired
    private MedicoRepository repository;

    @PostMapping
    @Transactional
    public ResponseEntity cadastrar(@RequestBody @Valid DadosCadastroMedico dados, UriComponentsBuilder uriBuilder) {
        var medico = new Medico(dados);
        repository.save(medico);

        //a linha abaixo cria uma url usando a classe UriComponentsBuilder para devolver uma URL com id do médico
        var uri = uriBuilder.path("/medicos/{id}").buildAndExpand(medico.getId()).toUri();

        return ResponseEntity.created(uri).body(new DadosDetalhamentoMedico(medico));
    }
    //ResponseEntity é o que controla a resposta que será enviada para o front
    //o ideal pensando em boas práticas, para que o método não retorne um valor padrão
    //que seria difícil de identificar o comportamento da solicitação
    //além de mandar um "corpo na resposta" - como os dados do cadastro que foi atualizado/cadastrado, por exemplo


    @GetMapping
    public ResponseEntity<Page<DadosListaMedico>> listar(@PageableDefault(size = 10, sort = {"nome"}) Pageable pageable) {
        //Page gera os dados de paginação para o front-end, usado junto com o Pageable,
        //cuidar ao selecionar, para pegar o do spring
        var page = repository.findAllByAtivoTrue(pageable)
                .map(DadosListaMedico::new);
        return ResponseEntity.ok(page);

        //esse método já tem um "stream" e gera um "tolist", itens que seriam adicionados
        //caso devolvesse um "List" com uso do findAll + stream
    }
    //@PageableDefault é uma forma de mandar os dados já com alguns parâmetros
    //ele é sobre escrito se a url vier do front com parâmetros
    //exemplo: http://localhost:8080/medicos?sort=crm,desc&size=2


    @PutMapping
    @Transactional
    public ResponseEntity atualizar(@RequestBody @Valid DadosAtualizaMedico dados) {
        var medico = repository.getReferenceById(dados.id());
        medico.atualizarInformacoes(dados);

        return ResponseEntity.ok(new DadosDetalhamentoMedico(medico));
    }
    //foi criado um novo DTO de médico para devolver no front os dados do médico
    //depois de feita a atualização dos dados.

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
    public ResponseEntity excluir(@PathVariable Long id) {
        var medico = repository.getReferenceById(id);
        medico.excluir();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity detalhar(@PathVariable Long id) {
        var medico = repository.getReferenceById(id);

        return ResponseEntity.ok(new DadosDetalhamentoMedico(medico));
    }

}



