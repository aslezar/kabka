package dev.kabka.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
class MessageFlowIntegrationTest {

	@Autowired
	private TestRestTemplate restTemplate;

	@SuppressWarnings("unchecked")
	private Map<String, Object> postForMap(String url) {
		return restTemplate.postForObject(url, null, Map.class);
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getForMap(String url) {
		return restTemplate.getForObject(url, Map.class);
	}

	@Test
	void pollTwiceWithoutCommitReturnsSameBatchThenCommitAdvances() {
		postForMap("/api/messages/push?topic=example-topic&partition=0&message=integration-test-message");

		String pollUrl = "/api/messages/poll?topic=example-topic&partition=0&group=consumer-group-1&batchSize=10";
		Map<String, Object> first = getForMap(pollUrl);
		Map<String, Object> second = getForMap(pollUrl);
		assertThat(first.get("messages")).isEqualTo(second.get("messages"));

		long committedOffset = ((Number) first.get("committedOffset")).longValue();
		long newOffset = committedOffset + 1;
		postForMap("/api/messages/commit?topic=example-topic&partition=0&group=consumer-group-1&offset=" + newOffset);

		Map<String, Object> third = getForMap(pollUrl);
		assertThat(((Number) third.get("committedOffset")).longValue()).isEqualTo(newOffset);
	}
}
