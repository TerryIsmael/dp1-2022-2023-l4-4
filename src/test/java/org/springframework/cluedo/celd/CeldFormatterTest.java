package org.springframework.cluedo.celd;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cluedo.boardGraph.BoardGraph;
import org.springframework.cluedo.enumerates.CeldType;
import org.springframework.cluedo.exceptions.DataNotFound;
import org.springframework.context.annotation.ComponentScan;

@ExtendWith(MockitoExtension.class)
public class CeldFormatterTest {

    @Mock
    protected CeldService service;

    protected CeldFormatter formatter;

    List<Celd> celds = new ArrayList<>();

    @BeforeEach
    public void config(){
        
        Celd c1 = new Celd();
        Celd c2 = new Celd();
        Celd c3 = new Celd();
        Celd c4 = new Celd();

        c1.setId(1);
        c1.setPosition(1);
        c1.setCeldType(CeldType.CENTER);

        c2.setId(2);
        c2.setPosition(2);
        c2.setCeldType(CeldType.GUESTROOM);

        c3.setId(3);
        c3.setPosition(3);
        c3.setCeldType(CeldType.CORRIDOR);

        c4.setId(4);
        c4.setPosition(4);
        c4.setCeldType(CeldType.KITCHEN);

        c1.setConnectedCelds(List.of(c2,c3));
        c2.setConnectedCelds(List.of(c1,c4));
        c3.setConnectedCelds(List.of(c1,c4));
        c4.setConnectedCelds(List.of(c2,c3));

        celds.add(c1);
        celds.add(c2);
        celds.add(c3);
        celds.add(c4);

        formatter = new CeldFormatter(service);

    }

    @Test
    public void testPrint(){
        when(service.getAllCelds()).thenReturn(celds);
        List<Celd> celdss = service.getAllCelds();
        assertEquals(formatter.print(celdss.get(0), Locale.ENGLISH), "CENTER");
        assertEquals(formatter.print(celdss.get(1), Locale.ENGLISH), "GUEST ROOM");
        assertEquals(formatter.print(celdss.get(2), Locale.ENGLISH), "3");
        assertEquals(formatter.print(celdss.get(3), Locale.ENGLISH), "KITCHEN");
    }

    @Test
    public void testParse() throws DataNotFound, NumberFormatException, ParseException{
        when(service.getById(1)).thenReturn(celds.get(0));
        Celd c = formatter.parse("1", Locale.ENGLISH);
        assertEquals(c, celds.get(0));
    }

    //@Test
    public void testShouldParseException() throws DataNotFound{
        when(service.getById(0)).thenReturn(celds.get(0));
        assertThrows(ParseException.class, () -> {formatter.parse("0", Locale.ENGLISH);});
    }

    @Test
    public void testParse2() throws NumberFormatException, ParseException{
        when(service.getByCeldType(CeldType.CORRIDOR)).thenReturn(celds.get(2));
        Celd c = formatter.parse("CORRIDOR", Locale.ENGLISH);
        assertEquals(c, celds.get(2));
    }

    @Test
    public void testIsNumericTrue(){
        assertTrue(CeldFormatter.isNumeric("69"));
    }

    @Test
    public void testIsNumericFalse(){
        assertFalse(CeldFormatter.isNumeric("CADENA DE TEXTO"));
    }
    
}