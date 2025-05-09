package io.github.Hayo87.services;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tno.gltsdiff.glts.lts.automaton.Automaton;

import io.github.Hayo87.controller.BadRequestException;

public class ParserServiceTest {

    private final ParserService parserService= new ParserService(new ObjectMapper());

    static Stream<Path> loadDotFiles() throws Exception{
        URI uri = Objects.requireNonNull(ParserServiceTest.class.getResource("/")).toURI();
        return Files.list(Path.of(uri))
            .filter(path -> path.toString().endsWith(".dot"));
    }

    @ParameterizedTest
    @MethodSource("loadDotFiles")
    void convertDotStringToJson_withValidInput_returnsJson(Path dotFile) throws Exception{
        String dotString = Files.readString(dotFile);

        Automaton<String> result = parserService.convertDotStringToAutomaton(dotString);
        assertNotNull(result);
        assertFalse(result.getStates().isEmpty());
    }

    @Test
    void convertDotStringToJson_withInvalidInput_throwsException(){
        String invalid_dotString = "digraph { A -> B [la";
        assertThrows(BadRequestException.class, 
            () -> parserService.convertDotStringToAutomaton(invalid_dotString));
        
    }



}
