package ru.biatech.voip.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.biatech.voip.model.Cdr;
import ru.biatech.voip.model.Extension;
import ru.biatech.voip.repo.AsteriskJdbcRepo;
import ru.biatech.voip.repo.VoipMonitorJdbcRepo;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AsteriskExtensionServiceImplTest {
    @Mock
    private AsteriskJdbcRepo asteriskJdbcRepo;
    @Mock
    private VoipMonitorJdbcRepo voipMonitorJdbcRepo;
    @InjectMocks
    private AsteriskExtensionServiceImpl asteriskExtensionServiceImpl;
    private static final String CALLED_1000000 = "1000000",
            CALLED_10000000000 = "10000000000",
            CALLER_1000001 = "1000001",
            CALLER_50000 = "50000",
            EXTEN_61000 = "61000",
            EXTEN_61001 = "61001";
    private static final Long ID_1 = 1L;


    @Test
    void processCdr_CdrCalledMatchesAndExtensionToReplaceExists_CdrModified() {
        //given
        Cdr cdr = Cdr.builder().called(CALLED_1000000).caller(CALLER_50000).Id(ID_1).build();
        Extension extension = Extension.builder().exten(EXTEN_61000).build();
        //when
        Mockito.when(asteriskJdbcRepo.getExtenByAppdata(CALLED_1000000)).thenReturn(Optional.of(extension));
        Mockito.when(voipMonitorJdbcRepo.updateCdrCalledById(ID_1, EXTEN_61000)).thenReturn(1);


        //then
        asteriskExtensionServiceImpl.processCdr(cdr);

        //assert
        verify(asteriskJdbcRepo, times(1)).getExtenByAppdata(any(String.class));
        verify(voipMonitorJdbcRepo, times(1)).updateCdrCalledById(any(Long.class), any(String.class));
    }

    @Test
    void processCdr_CdrCalledAndCallerMatchesAndExtensionToReplaceExists_CdrModified() {
        //given
        Cdr cdr = Cdr.builder().called(CALLED_1000000).caller(CALLER_1000001).Id(ID_1).build();
        Extension extension1 = Extension.builder().exten(EXTEN_61000).build(), extension2 = Extension.builder().exten(EXTEN_61001).build();

        //when
        Mockito.when(asteriskJdbcRepo.getExtenByAppdata(CALLED_1000000)).thenReturn(Optional.of(extension1));
        Mockito.when(asteriskJdbcRepo.getExtenByAppdata(CALLER_1000001)).thenReturn(Optional.of(extension2));
        Mockito.when(voipMonitorJdbcRepo.updateCdrCalledById(ID_1, EXTEN_61000)).thenReturn(1);
        Mockito.when(voipMonitorJdbcRepo.updateCdrCallerById(ID_1, EXTEN_61001)).thenReturn(1);

        //then
        asteriskExtensionServiceImpl.processCdr(cdr);

        //assert
        verify(asteriskJdbcRepo, times(2)).getExtenByAppdata(any(String.class));
        verify(voipMonitorJdbcRepo, times(1)).updateCdrCalledById(any(Long.class), any(String.class));
        verify(voipMonitorJdbcRepo, times(1)).updateCdrCallerById(any(Long.class), any(String.class));
    }

    @Test
    void processCdr_CdrCalledOrCallerMatchesButExtensionToReplaceDoesNotExist_CdrIsNotModified() {
        //given
        Cdr cdr = Cdr.builder().called(CALLED_1000000).caller(CALLER_1000001).Id(ID_1).build();
        // Extension extension1 = Extension.builder().exten(EXTEN_61000).build(), extension2 = Extension.builder().exten(EXTEN_61001).build();

        //when
        Mockito.when(asteriskJdbcRepo.getExtenByAppdata(CALLED_1000000)).thenReturn(Optional.empty());
        Mockito.when(asteriskJdbcRepo.getExtenByAppdata(CALLER_1000001)).thenReturn(Optional.empty());

        //then
        asteriskExtensionServiceImpl.processCdr(cdr);

        //assert
        verify(asteriskJdbcRepo, times(2)).getExtenByAppdata(any(String.class));
        verify(voipMonitorJdbcRepo, times(0)).updateCdrCalledById(any(Long.class), any(String.class));
        verify(voipMonitorJdbcRepo, times(0)).updateCdrCallerById(any(Long.class), any(String.class));
    }



    @Test
    void isCdrSuitable_CdrCalledStartsWith1AndLenghMoreThan7_ReturnsTrue() {
        //given
        Cdr cdr = Cdr.builder().called(CALLED_1000000).caller(CALLER_50000).build();
        //then
        boolean result = asteriskExtensionServiceImpl.isCdrSuitable(cdr);
        //assert
        assertThat(result).isTrue();
    }

    @Test
    void isCdrSuitable_CdrCallerStartsWith1AndLenghMoreThan7_ReturnsTrue() {
        //given
        Cdr cdr = Cdr.builder().caller(CALLER_1000001).called(CALLED_1000000).build();
        //then
        boolean result = asteriskExtensionServiceImpl.isCdrSuitable(cdr);
        //assert
        assertThat(result).isTrue();
    }

    @Test
    void isCdrSuitable_CdrCalledStartsWith1AndLenghMoreThan10_ReturnsFalse() {
        //given
        Cdr cdr = Cdr.builder().called(CALLED_10000000000).build();
        //when
        boolean result = asteriskExtensionServiceImpl.isCdrSuitable(cdr);
        //then
        assertThat(result).isFalse();
    }

    @Test
    void isCdrSuitable_CdrCallerStartsWith1AndLenghMoreThan10_ReturnsFalse() {
        //given
        Cdr cdr = Cdr.builder().caller(CALLED_10000000000).build();
        //when
        boolean result = asteriskExtensionServiceImpl.isCdrSuitable(cdr);
        //then
        assertThat(result).isFalse();
    }

    @Test
    void isCdrSuitable_CdrIsNull_ReturnsFalse() {
        //given
        Cdr cdr = Cdr.builder().build();
        //when
        boolean result = asteriskExtensionServiceImpl.isCdrSuitable(cdr);
        //then
        assertThat(result).isFalse();
    }
}