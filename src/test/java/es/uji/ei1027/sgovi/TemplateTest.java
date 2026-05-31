package es.uji.ei1027.sgovi;

import es.uji.ei1027.sgovi.dao.ActivitatFormacioDao;
import es.uji.ei1027.sgovi.dao.FormadorDao;
import es.uji.ei1027.sgovi.model.Formador;
import es.uji.ei1027.sgovi.model.UsuariOVI;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
public class TemplateTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FormadorDao formadorDao;

    @MockBean
    private ActivitatFormacioDao activitatFormacioDao;

    @Test
    public void testAddTemplate() throws Exception {
        when(formadorDao.getFormadors()).thenReturn(Collections.emptyList());

        UsuariOVI tecnic = new UsuariOVI();
        tecnic.setRol("tecnic");
        
        try {
            mockMvc.perform(get("/activitatformacio/add")
                    .sessionAttr("usuariLogat", tecnic))
                    .andDo(print());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
