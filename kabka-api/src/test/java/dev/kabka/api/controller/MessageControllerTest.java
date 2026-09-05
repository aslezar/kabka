package dev.kabka.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import dev.kabka.api.metrics.MeteredKabkaEngine;
import dev.kabka.core.exception.GroupNotAssignedException;
import dev.kabka.core.exception.InvalidOffsetException;
import dev.kabka.core.exception.TopicNotFoundException;
import dev.kabka.core.message.Message;
import dev.kabka.core.topic.PushResult;
import java.time.Instant;
import java.util.OptionalInt;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(MessageController.class)
class MessageControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private MeteredKabkaEngine engine;

	@Test
	void pushWithoutPartitionSucceeds() throws Exception {
		when(engine.pushToTopic(anyString(), any(byte[].class), any(OptionalInt.class)))
				.thenReturn(new PushResult(1, 0));

		mockMvc.perform(MockMvcRequestBuilders.post("/api/messages/push").param("topic", "t")
				.param("message", "hello").contentType(MediaType.APPLICATION_FORM_URLENCODED))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.partition").value(1));
	}

	@Test
	void pushToUnknownTopicReturns404WithCleanBody() throws Exception {
		when(engine.pushToTopic(anyString(), any(byte[].class), any(OptionalInt.class)))
				.thenThrow(new TopicNotFoundException("Topic not found: nope"));

		mockMvc.perform(MockMvcRequestBuilders.post("/api/messages/push").param("topic", "nope")
				.param("message", "x").contentType(MediaType.APPLICATION_FORM_URLENCODED))
				.andExpect(MockMvcResultMatchers.status().isNotFound())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Topic not found: nope"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.trace").doesNotExist());
	}

	@Test
	void pollHappyPath() throws Exception {
		when(engine.getCommittedOffset(anyString(), anyInt(), anyString())).thenReturn(0L);
		when(engine.pollFromGroup(anyString(), anyInt(), anyString(), anyInt()))
				.thenReturn(new Message[] { new Message("a".getBytes(), 0, Instant.now()) });

		mockMvc.perform(MockMvcRequestBuilders.get("/api/messages/poll").param("topic", "t").param("partition", "0")
				.param("group", "g1")).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.messages.length()").value(1));
	}

	@Test
	void pollForUnassignedCombinationReturns404() throws Exception {
		when(engine.getCommittedOffset(anyString(), anyInt(), anyString())).thenReturn(0L);
		when(engine.pollFromGroup(anyString(), anyInt(), anyString(), anyInt()))
				.thenThrow(new GroupNotAssignedException("Group g1 is not assigned to t/0"));

		mockMvc.perform(MockMvcRequestBuilders.get("/api/messages/poll").param("topic", "t").param("partition", "0")
				.param("group", "g1")).andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	void commitHappyPath() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/api/messages/commit").param("topic", "t").param("partition", "0")
				.param("group", "g1").param("offset", "1").contentType(MediaType.APPLICATION_FORM_URLENCODED))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value("committed"));
	}

	@Test
	void commitWithBadOffsetReturns400() throws Exception {
		doThrow(new InvalidOffsetException("Invalid commit offset -1 for partition 0")).when(engine)
				.commitOffset(anyString(), anyInt(), anyString(), anyLong());

		mockMvc.perform(MockMvcRequestBuilders.post("/api/messages/commit").param("topic", "t").param("partition", "0")
				.param("group", "g1").param("offset", "-1").contentType(MediaType.APPLICATION_FORM_URLENCODED))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}
}
