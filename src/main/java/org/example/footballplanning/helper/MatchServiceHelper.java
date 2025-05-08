package org.example.footballplanning.helper;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.example.footballplanning.bean.match.get.GetMatchResponseBean;
import org.example.footballplanning.model.child.MatchEnt;
import org.example.footballplanning.repository.MatchRepository;
import org.springframework.boot.autoconfigure.web.format.DateTimeFormatters;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;

import static org.example.footballplanning.util.GeneralUtil.*;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequiredArgsConstructor
public class MatchServiceHelper {
    MatchRepository matchRepository;

    public String mapFromMatchResponseToJson(MatchEnt match) {
        GetMatchResponseBean matchResponse = GetMatchResponseBean.builder()
                .matchDate(dateTimeToStr(match.getMatchDate()))
                .durationInMinutes(match.getDurationInMinutes())
                .costPerPlayer(match.getCostPerPlayer())
                .announcementId(match.getAnnouncement().getId())
                .build();
        return createJson(matchResponse);
    }

    @CachePut(value = "matchCache", key = "#result.id")
    public MatchEnt save(MatchEnt match) {
        return matchRepository.save(match);
    }


    @SneakyThrows
    public byte[] generateHtmlPoster(MatchEnt match) {
        String html= Files.readString(Paths.get("C:\\cavid\\Java Projects\\football-planning\\src\\main\\resources\\templates\\match-poster.html"));

        html=html.replace("${teamA}",match.getTeamA().getTeamName())
                .replace("${teamB}",match.getTeamB().getTeamName())
                .replace("${date}",match.getMatchDate().format(DateTimeFormatter.ofPattern("dd:MM:yyyy")))
                .replace("${stadium}",match.getStadium().getName())
                .replace("${cost}",String.format("%.2f",match.getCostPerPlayer()));

        ByteArrayOutputStream os=new ByteArrayOutputStream();
        PdfRendererBuilder builder=new PdfRendererBuilder();

        builder.withHtmlContent(html,null);
        builder.toStream(os);
        builder.run();

        return os.toByteArray();
    }
}