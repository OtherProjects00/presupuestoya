package com.presupuestoya.service;

import com.presupuestoya.domain.AppUser;
import com.presupuestoya.domain.Client;
import com.presupuestoya.repository.ClientRepository;
import com.presupuestoya.repository.QuoteRepository;
import com.presupuestoya.web.form.ClientForm;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class ClientService {

    private final ClientRepository clients;
    private final QuoteRepository quotes;

    public ClientService(ClientRepository clients, QuoteRepository quotes) {
        this.clients = clients;
        this.quotes = quotes;
    }

    @Transactional(readOnly = true)
    public List<Client> list(AppUser owner) {
        return clients.findByOwnerIdOrderByNameAsc(owner.getId());
    }

    @Transactional(readOnly = true)
    public Client get(AppUser owner, Long id) {
        return clients.findByIdAndOwnerId(id, owner.getId())
                .orElseThrow(() -> new NotFoundException("Cliente no encontrado."));
    }

    @Transactional
    public Client create(AppUser owner, ClientForm form) {
        Client client = new Client(owner, form.getName().trim());
        apply(client, form);
        return clients.save(client);
    }

    @Transactional
    public Client update(AppUser owner, Long id, ClientForm form) {
        Client client = get(owner, id);
        client.setName(form.getName().trim());
        apply(client, form);
        return client;
    }

    @Transactional
    public void delete(AppUser owner, Long id) {
        Client client = get(owner, id);
        if (quotes.countByOwnerIdAndClientId(owner.getId(), id) > 0) {
            throw new IllegalStateException("No se puede borrar un cliente que ya tiene presupuestos.");
        }
        clients.delete(client);
    }

    public ClientForm toForm(Client client) {
        ClientForm form = new ClientForm();
        form.setId(client.getId());
        form.setName(client.getName());
        form.setPhone(client.getPhone());
        form.setEmail(client.getEmail());
        form.setAddress(client.getAddress());
        form.setNotes(client.getNotes());
        return form;
    }

    private void apply(Client client, ClientForm form) {
        client.setPhone(clean(form.getPhone()));
        client.setEmail(clean(form.getEmail()));
        client.setAddress(clean(form.getAddress()));
        client.setNotes(clean(form.getNotes()));
    }

    private String clean(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
