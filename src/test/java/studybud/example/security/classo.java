package studybud.example.security;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Entity
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idClient;

    @Size(min = 2, max = 30, message = "nom doit etre entre 2 et 30")
    private String nom;

    @Pattern(
            regexp = "^(\\d{4}-){3}\\d{4}$",
            message = "cate doit etre xxxx-xxxx-xxxx-xxxxx"
    )
    private String carteBancaire;

    @Email(message = "format email invalide")
    @Size(max = 50)
    private String email;
}

// one to many
//type categorie
@OneToMany(mappedBy = "typeCategorie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
@JsonIgnoreProperties("typeCategorie")
private List<Categorie> categories;

//categorie
@ManyToOne
@JoinColumn(name = "typecat_id")
private Type_Categorie typeCategorie;


//many to many
//commande
@OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
@JsonManagedReference
private List<Ligne_cmd> ligneCmds = new ArrayList<>();

//produit
@OneToMany(mappedBy = "produit", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
@JsonIgnore
private List<Ligne_cmd> ligneCmds;

//lignecmd
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "idProd")
private Produit produit;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "idCmd")
@JsonBackReference
private Commande commande;


//Repository
@Repository
public interface ProduitRepository extends JpaRepository<Produit, Long> {

    @Query(value = "SELECT * FROM produit WHERE prix > ?1", nativeQuery = true)
    List<Produit> prodsupp100(double prix);
}

//Service
public interface ProduitService {
    Optional<Produit> getProduitById(long id);
    List<Produit> getAllProduits();
    void deleteProduit(long id);
    Produit addProduit(Produit produit);
    long count();
    List<Produit> prodsupp100(double prix); // New method
}

//ServiceImpliment
@Service
public class ProduitServiceImpl implements ProduitService {

    private final ProduitRepository produitRepository;

    @Autowired
    public ProduitServiceImpl(ProduitRepository produitRepository) {
        this.produitRepository = produitRepository;
    }

    @Override
    public Optional<Produit> getProduitById(long id) {
        return produitRepository.findById(id);
        .findAll() , .deleteById(id), .save(produit), .count(), prodsupp100(prix)
    }


    //Controller
    @RestController
    @CrossOrigin
    @RequestMapping("/Produits")
    public class ProduitController {

        private final ProduitService produitService;

        @Autowired
        public ProduitController(ProduitService produitService) {
            this.produitService = produitService;
        }

        @GetMapping("/count")
        public long countProduits() {
            return produitService.count();
        }
        @GetMapping("/{id}")   @GetMapping      @DeleteMapping("/{id}")     @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)