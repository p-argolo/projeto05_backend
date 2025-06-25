package com.BaneseLabes.LocalSeguro.service;

import com.BaneseLabes.LocalSeguro.model.Client;
import com.BaneseLabes.LocalSeguro.model.SafetyPlace;
import com.BaneseLabes.LocalSeguro.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final ClientService clientService;
    private final MongoTemplate mongoTemplate;

    @Autowired
    public UserService(ClientService clientService, MongoTemplate mongoTemplate) {
        this.clientService = clientService;
        this.mongoTemplate = mongoTemplate;
    }

    // Retorna o nome da coleção MongoDB associada ao CNPJ do cliente
    public String resolveCollection(String cnpj) throws Exception {
        Optional<Client> client = clientService.findByCnpj(cnpj);
        if (client.isEmpty()) {
            throw new Exception("Client not found");
        }
        return client.get().getName();
    }

    // Salva um novo usuário na coleção correspondente ao cnpj
    public User save(User user, String cnpj) throws Exception {
        String collectionName = resolveCollection(cnpj);
        return mongoTemplate.save(user, collectionName);
    }

    // Retorna todos os usuários da coleção associada ao CNPJ
    public List<User> findAll(String cnpj) throws Exception {
        String collectionName = resolveCollection(cnpj);
        return mongoTemplate.findAll(User.class, collectionName);
    }

    // Busca um usuário pelo clientId na coleção associada ao CNPJ
    public Optional<User> findByClientId(String cnpj, String clientId) throws Exception {
        String collectionName = resolveCollection(cnpj);
        Query query = new Query(Criteria.where("clientId").is(clientId));
        User user = mongoTemplate.findOne(query, User.class, collectionName);
        return Optional.of(user);
    }

    // Busca um usuário pelo id na coleção associada ao CNPJ
    public User findById(String cnpj, String id) throws Exception {
        String collectionName = resolveCollection(cnpj);
        User user = mongoTemplate.findById(id, User.class, collectionName);
        if (user == null) {
            throw new Exception("User not found");
        }
        return user;
    }

    // Atualiza um usuário existente com novos dados
    public Optional<User> updateUser(String cnpj, String id, User newUserData) throws Exception {
        String collectionName = resolveCollection(cnpj);
        User existingUser = mongoTemplate.findById(id, User.class, collectionName);
        if (existingUser != null) {

            if (newUserData.getClientId() != null) {
                existingUser.setClientId(newUserData.getClientId());
            }
            if (newUserData.getSafetyPlaces() != null && !newUserData.getSafetyPlaces().isEmpty()) {
                existingUser.setSafetyPlaces(newUserData.getSafetyPlaces());
            }
            if (newUserData.getAuthorizationOutSafetyPlace() != null) {
                existingUser.setAuthorizationOutSafetyPlace(newUserData.getAuthorizationOutSafetyPlace());
            }

            mongoTemplate.save(existingUser, collectionName);
            return Optional.of(existingUser);
        }
        return Optional.empty();
    }

    // Deleta um usuário pelo id da coleção associada ao CNPJ
    public void deleteUser(String cnpj, String id) throws Exception {
        String collectionName = resolveCollection(cnpj);
        Query query = new Query(Criteria.where("id").is(id));
        mongoTemplate.remove(query, User.class, collectionName);
    }

    // Retorna os locais seguros de um usuário
    public List<SafetyPlace> getSafetyPlaces(User user) {
        return user.getSafetyPlaces();
    }

    // Busca um local seguro por ID dentro da lista do usuário
    public SafetyPlace findSafetyPlaceById(String id, User user) {
        for (SafetyPlace safetyPlace : user.getSafetyPlaces()) {
            if (id.equals(safetyPlace.getId())) {
                return safetyPlace;
            }
        }
        return null;
    }

    // Verifica se o usuário pode fazer Pix fora de local seguro
    public boolean canMakePixOutSafetyPlace(User user) {
        return user.getAuthorizationOutSafetyPlace().getPix().compareTo(BigDecimal.ZERO) != 0;
    }

    // Verifica se o usuário pode fazer empréstimo fora de local seguro
    public boolean canMakeLoanOutSafetyPlace(User user) {
        return user.getAuthorizationOutSafetyPlace().getLoan().compareTo(BigDecimal.ZERO) != 0;
    }

    // Verifica se o usuário pode fazer fora de local seguro
    public boolean canMakeBankSplitOutSafetyPlace(User user) {
        return user.getAuthorizationOutSafetyPlace().getBanksplit().compareTo(BigDecimal.ZERO) != 0;
    }

    // Verifica se o usuário pode fazer TED fora de local seguro
    public boolean canMakeTedOutSafetyPlace(User user) {
        return user.getAuthorizationOutSafetyPlace().getTed().compareTo(BigDecimal.ZERO) != 0;
    }

    // Verifica se o usuário pode registrar cartao virtual fora de local seguro
    public boolean canRegisterVirtualCard(User user) {
        return user.getAuthorizationOutSafetyPlace().canRegisterVirtualCard();
    }

    // Verifica se o usuário pode alterar a senha fora de local seguro
    public boolean canChangePassword(User user) {
        return user.getAuthorizationOutSafetyPlace().canChangePassword();
    }
}