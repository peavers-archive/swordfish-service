package space.swordfish.node.service.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import space.swordfish.common.json.services.JsonTransformService;
import space.swordfish.common.notification.services.NotificationService;
import space.swordfish.node.service.domain.Snapshot;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Collections;

@Service
public class DownloadServiceImpl implements DownloadService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private NotificationService notificationService;

    private JsonTransformService jsonTransformService;

    @Override
    public ResponseEntity<byte[]> downloadSnapshot(Snapshot snapshot) {
        String downloadLink = snapshot.getLinks().getLink("download_link").toString();

        notificationService.send("restore_event", "download_info", jsonTransformService.write(snapshot));

        ResponseEntity<byte[]> response = restTemplate.exchange(downloadLink, HttpMethod.GET, downloadHeaders(), byte[].class, "1");

        if (response.getStatusCode() == HttpStatus.OK) {
            return response;
        }

        return null;
    }

    @Override
    public String writeSnapshot(ResponseEntity<byte[]> response, Snapshot snapshot) {
        try {
            String filename = snapshot.getStackId() + "-" + snapshot.getMode() + "-" + Instant.now().getEpochSecond() + ".sspak";
            Files.write(Paths.get("/tmp/silverstripe/" + filename), response.getBody());
            return filename;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private HttpEntity<String> downloadHeaders() {
        restTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter());

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        return new HttpEntity<>(headers);
    }
}