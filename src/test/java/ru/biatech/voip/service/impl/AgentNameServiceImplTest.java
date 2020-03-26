package ru.biatech.voip.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.biatech.voip.model.Cdr;
import ru.biatech.voip.repo.AwdbJdbcRepo;
import ru.biatech.voip.repo.HdsJdbcRepo;
import ru.biatech.voip.repo.VoipMonitorJdbcRepo;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AgentNameServiceImplTest {
    @Mock
    private VoipMonitorJdbcRepo voipMonitorJdbcRepo;
    @Mock
    private HdsJdbcRepo hdsJdbcRepo;
    @Mock
    private AwdbJdbcRepo awdbJdbcRepo;

    @InjectMocks
    private AgentNameServiceImpl agentNameServiceImpl;

    private static final String CALLED_61000 = "61000",
            CALLER_51000 = "51000",
            CALLER_61001 = "61001",
            CALLED_51001 = "51001";


    @Test
    void isCdrSuitable_CdrMatches_ReturnsTrue() {
        //given
        Cdr cdr1 = Cdr.builder().called(CALLED_61000).caller(CALLER_51000).build(),
                cdr2 = Cdr.builder().called(CALLED_51001).caller(CALLER_61001).build(),
                cdr3 = Cdr.builder().called(CALLED_51001).caller(CALLER_51000).build();

        //then
        boolean result1 = agentNameServiceImpl.isCdrSuitable(cdr1),
                result2 = agentNameServiceImpl.isCdrSuitable(cdr2),
                result3 = agentNameServiceImpl.isCdrSuitable(cdr3);

        //assert
        assertThat(result1).isTrue();
        assertThat(result2).isTrue();
        assertThat(result3).isTrue();
    }

    @Test
    void isCdrSuitable_CdrDoesNotMatch_ReturnsFalse() {
        //given
        Cdr cdr4 = Cdr.builder().called(CALLED_61000).caller(CALLER_61001).build(),
                cdr5 = Cdr.builder().called(CALLED_51001).caller(null).build(),
                cdr6 = Cdr.builder().called(null).caller(CALLER_51000).build();
        //then
        boolean result4 = agentNameServiceImpl.isCdrSuitable(cdr4),
                result5 = agentNameServiceImpl.isCdrSuitable(cdr5),
                result6 = agentNameServiceImpl.isCdrSuitable(cdr6);
        //assert
        assertThat(result4).isFalse();
        assertThat(result5).isFalse();
        assertThat(result6).isFalse();
    }


    @Test
    void getCdrListBetweenDates() {
    }

    @Test
    void getCdrListUntilDate_CdrsExist_ReturnsCdrList() {
        //given
        Stream<Cdr> cdrStream = Stream.generate(() ->
                Cdr.builder().build()).limit(10);
                        /*.Id(new RandomDataGenerator().nextLong(1L, 100000L))
                        .callDate(Timestamp
                                .valueOf(LocalDateTime.now()
                                        .minusMinutes(new RandomDataGenerator()
                                                .nextLong(1L, 59L))))
                        .build())*/

        List<Cdr> cdrList = new ArrayList<>();
        cdrStream.forEach(cdrList::add);
        assertThat(cdrList.size()).isEqualTo(10);


        //when
        Mockito.when(voipMonitorJdbcRepo.getLastCdrAgentId()).thenReturn(1L);
        Mockito.when(voipMonitorJdbcRepo.getMaxIdUntilDate(any(Timestamp.class))).thenReturn(100L);
        Mockito.when(voipMonitorJdbcRepo.getCdrsByLastIdAndId(any(Long.class), any(Long.class))).thenReturn(cdrList);

        //then
        List<Cdr> gotCdrList = agentNameServiceImpl.getCdrListUntilDate(Timestamp.valueOf(LocalDateTime.now()));

        //assert
        verify(voipMonitorJdbcRepo, times(1)).getLastCdrAgentId();
        verify(voipMonitorJdbcRepo, times(1)).getMaxIdUntilDate(any(Timestamp.class));
        verify(voipMonitorJdbcRepo, times(1)).getCdrsByLastIdAndId(any(Long.class), any(Long.class));
        assertThat(gotCdrList.isEmpty()).isFalse();
        assertThat(gotCdrList).isEqualTo(cdrList);


    }

    @Test
    void processCdr() {
    }
}