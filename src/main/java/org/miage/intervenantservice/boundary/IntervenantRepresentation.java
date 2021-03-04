package org.miage.intervenantservice.boundary;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.transaction.Transactional;
import org.miage.intervenantservice.entity.Intervenant;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController // point d'entrée de notre API
@RequestMapping(value="/intervenants", produces = MediaType.APPLICATION_JSON_VALUE)
@ExposesResourceFor(Intervenant.class)
public class IntervenantRepresentation {
    private IntervenantResource ir;

    public IntervenantRepresentation(IntervenantResource ir) {
        this.ir = ir;
    }

    // GET all
    @GetMapping // la méthode va répondre à la méthode HTTP GET
    public ResponseEntity<?> getAllintervenants() {
        Iterable<Intervenant> allIntervenants = ir.findAll();
        return ResponseEntity.ok(intervenantToResource(allIntervenants));
    }

    // GET one
    @GetMapping(value="/{intervenantId}")
    public ResponseEntity<?> getIntervenant(@PathVariable("intervenantId") String id) {
        return Optional.ofNullable(ir.findById(id))
                        .filter(Optional::isPresent)
                        .map(i -> ResponseEntity.ok(intervenantToResource(i.get(),true)))
                        .orElse(ResponseEntity.notFound().build());
    }

    // POST
    @PostMapping
    @Transactional
    public ResponseEntity<?> saveIntervenant(@RequestBody Intervenant intervenant) {
        Intervenant intervenant2Save = new Intervenant(
            UUID.randomUUID().toString(),
            intervenant.getNom(),
            intervenant.getPrenom(),
            intervenant.getCommune(),
            intervenant.getCodepostal()
        );
        Intervenant saved = ir.save(intervenant2Save);
        URI location = linkTo(IntervenantRepresentation.class).slash(saved.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

        // DELETE
        @DeleteMapping(value="/{intervenantId}")
        @Transactional
        public ResponseEntity<?> deleteIntervenant(@PathVariable("intervenantId") String id) {
            Optional<Intervenant> intervenant = ir.findById(id);
            if (intervenant.isPresent()) {
                ir.delete(intervenant.get());
            }
            return ResponseEntity.noContent().build();
        }

        // PUT
        @PutMapping(value="/{intervenantId}")
        @Transactional
        public ResponseEntity<?> updateIntervenant(@RequestBody Intervenant intervenant,
                                    @PathVariable("intervenantId") String intervenantId) {
                Optional<Intervenant> body = Optional.ofNullable(intervenant);
                if (!body.isPresent()) {
                    return ResponseEntity.badRequest().build();
                }
                if (!ir.existsById(intervenantId)) {
                    return ResponseEntity.notFound().build();
                }
                intervenant.setId(intervenantId);
                Intervenant result = ir.save(intervenant);
                return ResponseEntity.ok().build();
            }

            // PATCH
            @PatchMapping(value="/{intervenantId}")
            @Transactional
            public ResponseEntity<?> updatePartielIntervenant(@RequestBody Map<Object,Object> champsJson,
                                        @PathVariable("intervenantId") String intervenantId) {
                Optional<Intervenant> body = ir.findById(intervenantId);
                if (body.isPresent()) {
                    Intervenant intervenant = body.get();
                    champsJson.forEach((f,v) -> {
                        Field champ = ReflectionUtils.findField(Intervenant.class, f.toString());
                        champ.setAccessible(true);
                        ReflectionUtils.setField(champ, intervenant, v);
                    });
                    intervenant.setId(intervenantId);
                    ir.save(intervenant);
                    return ResponseEntity.ok().build();
               }
                return ResponseEntity.notFound().build();
            }
                                        
    private EntityModel<Intervenant> intervenantToResource(Intervenant intervenant, Boolean isCollection)  {
        Link selfLink = linkTo(IntervenantRepresentation.class).slash(intervenant.getId()).withSelfRel();
        if (isCollection) {
            Link collectionLink = linkTo(methodOn(IntervenantRepresentation.class).getAllintervenants())
                                            .withRel("collection");
            return  EntityModel.of(intervenant, selfLink, collectionLink);
        } else {
            return  EntityModel.of(intervenant, selfLink);
        }
    }

    private CollectionModel<EntityModel<Intervenant>> intervenantToResource(Iterable<Intervenant> intervenants) {
        Link selfLink = linkTo(methodOn(IntervenantRepresentation.class).getAllintervenants()).withSelfRel();
        List<EntityModel<Intervenant>> intervenantsResource = new ArrayList<>();
        intervenants.forEach(i -> 
            intervenantsResource.add(intervenantToResource(i, false)));
        return CollectionModel.of(intervenantsResource, selfLink);
    }

}
