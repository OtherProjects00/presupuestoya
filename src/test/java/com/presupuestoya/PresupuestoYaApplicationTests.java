package com.presupuestoya;

import com.presupuestoya.domain.AppUser;
import com.presupuestoya.domain.Client;
import com.presupuestoya.domain.Quote;
import com.presupuestoya.service.BusinessProfileService;
import com.presupuestoya.service.ClientService;
import com.presupuestoya.service.QuoteService;
import com.presupuestoya.service.UserService;
import com.presupuestoya.web.form.ClientForm;
import com.presupuestoya.web.form.LineItemForm;
import com.presupuestoya.web.form.QuoteForm;
import com.presupuestoya.web.form.RegisterForm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PresupuestoYaApplicationTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserService userService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private QuoteService quoteService;

    @Autowired
    private BusinessProfileService profileService;

    @Test
    void publicPagesRenderWithoutErrors() throws Exception {
        mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("PresupuestoYa")));

        mvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Ingresar")))
                .andExpect(content().string(containsString("data-toggle-password")));

        mvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Crear cuenta")))
                .andExpect(content().string(containsString("aria-controls=\"password\"")))
                .andExpect(content().string(containsString("aria-controls=\"confirmPassword\"")));

        // La fuente es un recurso público: la landing y el login la necesitan sin sesión.
        mvc.perform(get("/fonts/roboto-latin.woff2"))
                .andExpect(status().isOk());
    }

    @Test
    void securityHeadersArePresent() throws Exception {
        mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Security-Policy", containsString("default-src 'self'")))
                .andExpect(header().string("X-Frame-Options", "DENY"))
                .andExpect(header().string("Referrer-Policy", "no-referrer"))
                .andExpect(header().string("Permissions-Policy", containsString("camera=()")));
    }

    @Test
    void privateAreaRequiresLogin() throws Exception {
        mvc.perform(get("/app"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    void registrationRequiresCsrfAndCreatesAccount() throws Exception {
        String email = uniqueEmail("registro");

        mvc.perform(post("/register")
                        .param("displayName", "Taller Test")
                        .param("email", email)
                        .param("password", "clave-larga-segura")
                        .param("confirmPassword", "clave-larga-segura"))
                .andExpect(status().isForbidden());

        mvc.perform(post("/register")
                        .with(csrf())
                        .param("displayName", "Taller Test")
                        .param("email", email)
                        .param("password", "clave-larga-segura")
                        .param("confirmPassword", "clave-larga-segura"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void userOnlySeesOwnQuotes() throws Exception {
        AppUser owner = createUser("duenio");
        AppUser other = createUser("otro");

        Client client = createClient(owner, "Cliente del duenio");
        Quote quote = createQuote(owner, client);

        mvc.perform(get("/app/quotes/" + quote.getId()).with(user(owner.getEmail())))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(quote.getNumber())));

        mvc.perform(get("/app/quotes/" + quote.getId()).with(user(other.getEmail())))
                .andExpect(status().isNotFound());

        mvc.perform(get("/app/clients/" + client.getId() + "/edit").with(user(other.getEmail())))
                .andExpect(status().isNotFound());
    }

    @Test
    void settingsUpdateNormalizesCurrency() throws Exception {
        AppUser owner = createUser("configuracion");

        mvc.perform(post("/app/settings")
                        .with(user(owner.getEmail()))
                        .with(csrf())
                        .param("businessName", "Negocio Configuración")
                        .param("currency", "usd")
                        .param("defaultTaxRate", "21")
                        .param("defaultValidityDays", "15"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/app/settings"));

        assertThat(profileService.get(owner).getCurrency()).isEqualTo("USD");
    }

    @Test
    void canCreateClientAndQuoteThroughWebForms() throws Exception {
        AppUser owner = createUser("web");

        mvc.perform(post("/app/clients/save")
                        .with(user(owner.getEmail()))
                        .with(csrf())
                        .param("name", "Cliente Web")
                        .param("phone", "+54 9 11 4444-4444"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/app/clients"));

        Client client = clientService.list(owner).get(0);

        mvc.perform(post("/app/quotes/save")
                        .with(user(owner.getEmail()))
                        .with(csrf())
                        .param("clientId", client.getId().toString())
                        .param("issueDate", LocalDate.now().toString())
                        .param("validUntil", LocalDate.now().plusDays(15).toString())
                        .param("taxRate", "21")
                        .param("items[0].description", "Trabajo web")
                        .param("items[0].quantity", "2")
                        .param("items[0].unitPrice", "1500.50"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/app/quotes/*"));

        Quote quote = quoteService.list(owner, null).get(0);
        assertThat(quoteService.list(owner, null)).hasSize(1);
        assertThat(quote.getSubtotal()).isEqualByComparingTo("3001.00");
        assertThat(quote.getTaxAmount()).isEqualByComparingTo("630.21");
        assertThat(quote.getTotal()).isEqualByComparingTo("3631.21");
    }

    @Test
    void quoteCreationGuidesUserThroughCreatingFirstClient() throws Exception {
        AppUser owner = createUser("primercliente");

        mvc.perform(get("/app/quotes/new").with(user(owner.getEmail())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/app/clients/new?forQuote=true"));

        mvc.perform(get("/app/clients/new")
                        .param("forQuote", "true")
                        .with(user(owner.getEmail())))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("este cliente ya va a estar seleccionado")))
                .andExpect(content().string(containsString("forQuote=true")));

        MvcResult clientSave = mvc.perform(post("/app/clients/save")
                        .with(user(owner.getEmail()))
                        .with(csrf())
                        .param("forQuote", "true")
                        .param("name", "Cliente del primer presupuesto"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/app/clients"))
                .andReturn();

        Client client = clientService.list(owner).get(0);
        mvc.perform(get("/app/clients")
                        .with(user(owner.getEmail()))
                        .flashAttrs(clientSave.getFlashMap()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("id=\"quotePrompt\"")))
                .andExpect(content().string(containsString("clientId=" + client.getId())));

        mvc.perform(get("/app/quotes/new")
                        .param("clientId", client.getId().toString())
                        .with(user(owner.getEmail())))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("value=\"" + client.getId() + "\" selected")));
    }

    @Test
    void publicQuoteLinkWorksAndCanBeRevoked() throws Exception {
        AppUser owner = createUser("publico");
        Client client = createClient(owner, "Cliente público");
        Quote quote = createQuote(owner, client);
        String token = quoteService.createPublicLink(owner, quote.getId());

        mvc.perform(get("/q/" + token))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(quote.getNumber())))
                .andExpect(content().string(containsString("Cliente público")))
                .andExpect(content().string(containsString("1.210,00")))
                .andExpect(header().string("Cache-Control", containsString("no-store")))
                .andExpect(header().string("X-Robots-Tag", containsString("noindex")));

        quoteService.revokePublicLink(owner, quote.getId());

        mvc.perform(get("/q/" + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void dashboardAndQuotePagesRenderForAuthenticatedUser() throws Exception {
        AppUser owner = createUser("paginas");
        Client client = createClient(owner, "Cliente de páginas");
        Quote quote = createQuote(owner, client);

        mvc.perform(get("/app").with(user(owner.getEmail())))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Últimos presupuestos")));

        mvc.perform(get("/app/quotes").with(user(owner.getEmail())))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(quote.getNumber())))
                .andExpect(content().string(containsString("1.210,00")));

        mvc.perform(get("/app/quotes/" + quote.getId()).with(user(owner.getEmail())))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Generar enlace público")));

        mvc.perform(get("/app/quotes/" + quote.getId() + "/print").with(user(owner.getEmail())))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Imprimir / Guardar PDF")));
    }

    @Test
    void formPagesRenderForAuthenticatedUser() throws Exception {
        AppUser owner = createUser("formularios");
        Client client = createClient(owner, "Cliente de formularios");

        mvc.perform(get("/app/clients/new").with(user(owner.getEmail())))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Nuevo cliente")));

        mvc.perform(get("/app/clients/" + client.getId() + "/edit").with(user(owner.getEmail())))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Cliente de formularios")));

        mvc.perform(get("/app/quotes/new").with(user(owner.getEmail())))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Nuevo presupuesto")))
                .andExpect(content().string(containsString("data-prevent-double-submit")));

        mvc.perform(get("/app/settings").with(user(owner.getEmail())))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Mi negocio")));
    }

    private AppUser createUser(String prefix) {
        RegisterForm form = new RegisterForm();
        form.setDisplayName("Negocio " + prefix);
        form.setEmail(uniqueEmail(prefix));
        form.setPassword("clave-larga-segura");
        form.setConfirmPassword("clave-larga-segura");
        return userService.register(form);
    }

    private Client createClient(AppUser owner, String name) {
        ClientForm form = new ClientForm();
        form.setName(name);
        form.setPhone("+54 9 11 5555-5555");
        return clientService.create(owner, form);
    }

    private Quote createQuote(AppUser owner, Client client) {
        QuoteForm form = new QuoteForm();
        form.setClientId(client.getId());
        form.setIssueDate(LocalDate.now());
        form.setValidUntil(LocalDate.now().plusDays(15));
        form.setTaxRate(new BigDecimal("21.00"));
        LineItemForm item = form.getItems().get(0);
        item.setDescription("Trabajo de prueba");
        item.setQuantity(BigDecimal.ONE);
        item.setUnitPrice(new BigDecimal("1000.00"));
        return quoteService.create(owner, form);
    }

    private String uniqueEmail(String prefix) {
        return prefix + "-" + System.nanoTime() + "@example.com";
    }
}
