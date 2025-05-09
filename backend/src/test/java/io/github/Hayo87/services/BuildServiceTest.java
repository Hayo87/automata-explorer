package io.github.Hayo87.services;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.tno.gltsdiff.glts.lts.automaton.Automaton;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomaton;

import io.github.Hayo87.controller.BadRequestException;
import io.github.Hayo87.domain.handlers.DiffHandler;
import io.github.Hayo87.dto.BuildRequestDTO;
import io.github.Hayo87.dto.BuildResponseDTO;

@ExtendWith(MockitoExtension.class)
public class BuildServiceTest {

    @Mock
    private SessionService sessionService;

    @Mock
    private ParserService parserService;

    @Mock
    private HandlerService handlerService;

    @InjectMocks
    private BuildService buildService;

    String sessionId;
    String dotReference;
    String dotSubject; 
    Automaton<String> reference;
    Automaton<String> subject;
    SessionData session;

    @BeforeEach
        void setup() { 
        sessionId = "abc-123";
        dotReference = "dot ref";
        dotSubject = "dot sub";
        reference = mock(Automaton.class);
        subject = mock(Automaton.class);
        session = mock(SessionData.class);

        when(sessionService.getSession(sessionId)).thenReturn(session);
    }
    
    @Test
    void buildInputs_whenInputIsValid_storesParsedAutomaton(){
        when(session.getRawReference()).thenReturn(dotReference);
        when(session.getRawSubject()).thenReturn(dotSubject);
        when(parserService.convertDotStringToAutomaton(dotReference)).thenReturn(reference);
        when(parserService.convertDotStringToAutomaton(dotSubject)).thenReturn(subject);

        buildService.buildInputs(sessionId);

        verify(session).setReference(reference);
        verify(session).setSubject(subject);
        verify(parserService).convertDotStringToAutomaton(dotReference);
        verify(parserService).convertDotStringToAutomaton(dotSubject);
    }

    @Test
    void buildInputs_whenInputIsInvalid_throwsException(){
        when(session.getRawReference()).thenReturn(dotReference);
        when(session.getRawSubject()).thenReturn(dotSubject);
        when(parserService.convertDotStringToAutomaton(dotReference)).thenReturn(reference);
        when(parserService.convertDotStringToAutomaton(dotSubject)).thenThrow(BadRequestException.class);

        assertThrows(BadRequestException.class, () -> buildService.buildInputs(sessionId));
    }

    @Test
    void buildDiff_whenValidInput_returnsBuildResponseDTO(){
        BuildRequestDTO request = new BuildRequestDTO(List.of());

        DiffAutomaton<String> diff = mock(DiffAutomaton.class);
        DiffAutomaton<String> refDiff = mock(DiffAutomaton.class);
        DiffAutomaton<String> subDiff = mock(DiffAutomaton.class);
        DiffHandler handler = mock(DiffHandler.class);

        when(session.getReference()).thenReturn(reference);
        when(session.getSubject()).thenReturn(subject);
        when(handlerService.getHandler(any())).thenReturn(handler);
        when(handler.convert(reference, true)).thenReturn(refDiff);
        when(handler.convert(subject, false)).thenReturn(subDiff);
        when(handler.preProcessing(refDiff, request.actions())).thenReturn(refDiff);
        when(handler.preProcessing(subDiff, request.actions())).thenReturn(subDiff);
        when(handler.build(refDiff, subDiff)).thenReturn(diff);
        when(handler.postProcessing(diff, request.actions())).thenReturn(diff);

        BuildResponseDTO response = buildService.buildDiff(sessionId, request);

        assertNotNull(response);
        verify(handler).convert(reference, true);
        verify(handler).convert(subject, false);
        verify(handler).preProcessing(refDiff, request.actions());
        verify(handler).preProcessing(subDiff, request.actions());
        verify(handler).build(refDiff, subDiff);
        verify(handler).postProcessing(diff, request.actions());      
    }

    @Test
    void buildDiff_whenHandlerNotFound_throwsException(){
        BuildRequestDTO request = new BuildRequestDTO(List.of());
        when(handlerService.getHandler(any())).thenThrow(IllegalArgumentException.class);

        assertThrows(IllegalArgumentException.class, () -> buildService.buildDiff(sessionId, request));
    }

    @Test
    void buildDiff_whenPreProcessingFails_throwsException(){
        BuildRequestDTO request = new BuildRequestDTO(List.of());

        DiffAutomaton<String> diff = mock(DiffAutomaton.class);
        DiffAutomaton<String> refDiff = mock(DiffAutomaton.class);
        DiffAutomaton<String> subDiff = mock(DiffAutomaton.class);
        DiffHandler handler = mock(DiffHandler.class);

        when(session.getReference()).thenReturn(reference);
        when(session.getSubject()).thenReturn(subject);
        when(handlerService.getHandler(any())).thenReturn(handler);
        when(handler.convert(reference, true)).thenReturn(refDiff);
        when(handler.convert(subject, false)).thenReturn(subDiff);
        when(handler.preProcessing(refDiff, request.actions())).thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class, () -> buildService.buildDiff(sessionId, request));
    }

    @Test
    void buildDiff_whenBuildFails_throwsException(){
        BuildRequestDTO request = new BuildRequestDTO(List.of());

        DiffAutomaton<String> diff = mock(DiffAutomaton.class);
        DiffAutomaton<String> refDiff = mock(DiffAutomaton.class);
        DiffAutomaton<String> subDiff = mock(DiffAutomaton.class);
        DiffHandler handler = mock(DiffHandler.class);

        when(session.getReference()).thenReturn(reference);
        when(session.getSubject()).thenReturn(subject);
        when(handlerService.getHandler(any())).thenReturn(handler);
        when(handler.convert(reference, true)).thenReturn(refDiff);
        when(handler.convert(subject, false)).thenReturn(subDiff);
        when(handler.preProcessing(refDiff, request.actions())).thenReturn(refDiff);
        when(handler.preProcessing(subDiff, request.actions())).thenReturn(subDiff);
        when(handler.build(refDiff, subDiff)).thenThrow(RuntimeException.class);
        
        assertThrows(RuntimeException.class, () -> buildService.buildDiff(sessionId, request));
    }

    @Test
    void buildDiff_whenPostProcessingFails_throwsException(){
        BuildRequestDTO request = new BuildRequestDTO(List.of());

        DiffAutomaton<String> diff = mock(DiffAutomaton.class);
        DiffAutomaton<String> refDiff = mock(DiffAutomaton.class);
        DiffAutomaton<String> subDiff = mock(DiffAutomaton.class);
        DiffHandler handler = mock(DiffHandler.class);

        when(session.getReference()).thenReturn(reference);
        when(session.getSubject()).thenReturn(subject);
        when(handlerService.getHandler(any())).thenReturn(handler);
        when(handler.convert(reference, true)).thenReturn(refDiff);
        when(handler.convert(subject, false)).thenReturn(subDiff);
        when(handler.preProcessing(refDiff, request.actions())).thenReturn(refDiff);
        when(handler.preProcessing(subDiff, request.actions())).thenReturn(subDiff);
        when(handler.build(refDiff, subDiff)).thenReturn(diff);
        when(handler.postProcessing(diff, request.actions())).thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class, () -> buildService.buildDiff(sessionId, request));
    }

}
