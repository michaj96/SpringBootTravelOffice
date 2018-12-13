/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package hello;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private TripRepository tripRepository;

	@Before
	public void deleteAllBeforeTestsCustomers() throws Exception {
		customerRepository.deleteAll();
		tripRepository.deleteAll();

	}
	@Before
	public void deleteAllBeforeTestsTrips() throws Exception {
		tripRepository.deleteAll();

	}


	@Test
	public void shouldReturnRepositoryIndexCustomers() throws Exception {

		mockMvc.perform(get("/")).andDo(print()).andExpect(status().isOk()).andExpect(
				jsonPath("$._links.customers").exists());
	}

	@Test
	public void shouldReturnRepositoryIndexTrips() throws Exception {

		mockMvc.perform(get("/")).andDo(print()).andExpect(status().isOk()).andExpect(
				jsonPath("$._links.trips").exists());
	}

	@Test
	public void shouldCreateEntityCustomer() throws Exception {

		mockMvc.perform(post("/customers").content(
				"{\"firstName\": \"Frodo\", \"lastName\":\"Baggins\", \"address\": \"Legnicka\", \"trip\":\"Liverpool\"}")).andExpect(
						status().isCreated()).andExpect(
								header().string("Location", containsString("customers/")));
	}

	@Test
	public void shouldCreateEntityTrip() throws Exception {

		mockMvc.perform(post("/trips").content(
				"{\"destination\": \"London\", \"startDate\":\"20.03.2018\", \"endDate\": \"29.03.2018\"}")).andExpect(
				status().isCreated()).andExpect(
				header().string("Location", containsString("trips/")));
	}

	@Test
	public void shouldRetrieveEntityCustomer() throws Exception {

		MvcResult mvcResult = mockMvc.perform(post("/customers").content(
				"{\"firstName\": \"Frodo\", \"lastName\":\"Baggins\", \"address\": \"Legnicka\", \"trip\":\"Liverpool\"}")).andExpect(
						status().isCreated()).andReturn();

		String location = mvcResult.getResponse().getHeader("Location");
		mockMvc.perform(get(location)).andExpect(status().isOk()).andExpect(
				jsonPath("$.firstName").value("Frodo")).andExpect(
						jsonPath("$.lastName").value("Baggins")).andExpect(
								jsonPath("$.address").value("Legnicka")).andExpect(
										jsonPath("$.trip").value("Liverpool"));

	}

	@Test
	public void shouldRetrieveEntityTrip() throws Exception {

		MvcResult mvcResult = mockMvc.perform(post("/trips").content(
				"{\"destination\": \"London\", \"startDate\":\"20.03.2018\", \"endDate\": \"29.03.2018\"}")).andExpect(
				status().isCreated()).andReturn();

		String location = mvcResult.getResponse().getHeader("Location");
		mockMvc.perform(get(location)).andExpect(status().isOk()).andExpect(
				jsonPath("$.destination").value("London")).andExpect(
				jsonPath("$.startDate").value("20.03.2018")).andExpect(
				jsonPath("$.endDate").value("29.03.2018"));

	}

	@Test
	public void shouldQueryEntityCustomers() throws Exception {

		mockMvc.perform(post("/customers").content(
				"{\"firstName\": \"Frodo\", \"lastName\":\"Baggins\", \"address\": \"Legnicka\", \"trip\":\"Liverpool\"}")).andExpect(
						status().isCreated());

		mockMvc.perform(
				get("/customers/search/findByLastName?name={name}", "Baggins")).andExpect(
						status().isOk()).andExpect(
								jsonPath("$._embedded.customers[0].firstName").value(
										"Frodo"));
	}

	@Test
	public void shouldQueryEntityTrips() throws Exception {

		mockMvc.perform(post("/trips").content(
				"{\"destination\": \"London\", \"startDate\":\"20.03.2018\", \"endDate\": \"29.03.2018\"}")).andExpect(
				status().isCreated());

		mockMvc.perform(
				get("/trips/search/findByDestination?destination={destination}", "London")).andExpect(
				status().isOk()).andExpect(
				jsonPath("$._embedded.trips[0].startDate").value(
						"20.03.2018"));
	}


	@Test
	public void shouldUpdateEntityCustomers() throws Exception {

		MvcResult mvcResult = mockMvc.perform(post("/customers").content(
				"{\"firstName\": \"Frodo\", \"lastName\":\"Baggins\", \"address\": \"Legnicka\", \"trip\":\"Liverpool\"}")).andExpect(
						status().isCreated()).andReturn();

		String location = mvcResult.getResponse().getHeader("Location");

		mockMvc.perform(put(location).content(
				"{\"firstName\": \"Bilbo\", \"lastName\":\"Baggins\"}")).andExpect(
						status().isNoContent());

		mockMvc.perform(get(location)).andExpect(status().isOk()).andExpect(
				jsonPath("$.firstName").value("Bilbo")).andExpect(
						jsonPath("$.lastName").value("Baggins"));
	}

	@Test
	public void shouldUpdateEntityTrips() throws Exception {

		MvcResult mvcResult = mockMvc.perform(post("/trips").content(
				"{\"destination\": \"London\", \"startDate\":\"20.03.2018\", \"endDate\": \"29.03.2018\"}")).andExpect(
				status().isCreated()).andReturn();

		String location = mvcResult.getResponse().getHeader("Location");

		mockMvc.perform(put(location).content(
				"{\"destination\": \"Liverpool\", \"startDate\":\"21.04.2018\", \"endDate\": \"30.04.2018\"}")).andExpect(
				status().isNoContent());

		mockMvc.perform(get(location)).andExpect(status().isOk()).andExpect(
				jsonPath("$.destination").value("Liverpool")).andExpect(
				jsonPath("$.startDate").value("21.04.2018")).andExpect(
						jsonPath("$.endDate").value("30.04.2018"));

	}

	@Test
	public void shouldPartiallyUpdateEntityCustomer() throws Exception {

		MvcResult mvcResult = mockMvc.perform(post("/customers").content(
				"{\"firstName\": \"Frodo\", \"lastName\":\"Baggins\", \"address\": \"Legnicka\", \"trip\":\"Liverpool\"}")).andExpect(
						status().isCreated()).andReturn();

		String location = mvcResult.getResponse().getHeader("Location");

		mockMvc.perform(
				patch(location).content("{\"firstName\": \"Bilbo Jr.\"}")).andExpect(
						status().isNoContent());

		mockMvc.perform(get(location)).andExpect(status().isOk()).andExpect(
				jsonPath("$.firstName").value("Bilbo Jr.")).andExpect(
						jsonPath("$.lastName").value("Baggins"));
	}

	@Test
	public void shouldPartiallyUpdateEntityTrip() throws Exception {

		MvcResult mvcResult = mockMvc.perform(post("/trips").content(
				"{\"destination\": \"London\", \"startDate\":\"20.03.2018\", \"endDate\": \"29.03.2018\"}")).andExpect(
				status().isCreated()).andReturn();

		String location = mvcResult.getResponse().getHeader("Location");

		mockMvc.perform(
				patch(location).content("{\"destination\": \"Liverpool\"}")).andExpect(
				status().isNoContent());

		mockMvc.perform(get(location)).andExpect(status().isOk()).andExpect(
				jsonPath("$.destination").value("Liverpool")).andExpect(
				jsonPath("$.startDate").value("20.03.2018")).andExpect(
				jsonPath("$.endDate").value("29.03.2018"));
	}

	@Test
	public void shouldDeleteEntityCustomer() throws Exception {

		MvcResult mvcResult = mockMvc.perform(post("/customers").content(
				"{\"firstName\": \"Frodo\", \"lastName\":\"Baggins\", \"address\": \"Legnicka\", \"trip\":\"Liverpool\"}")).andExpect(
						status().isCreated()).andReturn();

		String location = mvcResult.getResponse().getHeader("Location");
		mockMvc.perform(delete(location)).andExpect(status().isNoContent());

		mockMvc.perform(get(location)).andExpect(status().isNotFound());
	}

	@Test
	public void shouldDeleteEntityTrip() throws Exception {

		MvcResult mvcResult = mockMvc.perform(post("/trips").content(
				"{\"destination\": \"London\", \"startDate\":\"20.03.2018\", \"endDate\": \"29.03.2018\"}")).andExpect(
				status().isCreated()).andReturn();

		String location = mvcResult.getResponse().getHeader("Location");
		mockMvc.perform(delete(location)).andExpect(status().isNoContent());

		mockMvc.perform(get(location)).andExpect(status().isNotFound());
	}

}