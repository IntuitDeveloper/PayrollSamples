using System;
using System.IO;
using System.Json;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;
using Newtonsoft.Json;

namespace NetCoreV4Samples
{
	class IntuitV4Samples
	{

		const string intuitGraphQlEndpoint = "https://v4thirdparty-e2e.api.intuit.com/graphql";

		public static async Task MainAsync()
		{
			try
			{
				await readEmployeeCompensation();
				await readEmployerCompensation();
			}
			catch (Exception ex)
			{
				Console.WriteLine($"Error calling Intuit APIs: {ex.Message}");
			}
			finally { }
		}

		private static async Task readEmployeeCompensation()
		{
			try
			{
				Console.WriteLine("***Started Reading V4 Employee Compensation***");
				var employeeCompensationReadAllQuery = File.ReadAllText("IntuitGraphQL/payroll-read-employeeCompensation.graphql");
				JsonValue jsonResponse = await executeIntuitGraphQLRequest(employeeCompensationReadAllQuery, "", true);
				displayEmployeeCompensation(jsonResponse);
			}
			catch (Exception ex) { throw ex; }
			finally
			{
				Console.WriteLine("***Finished Reading V4 Employee Compensation***");
			}
		}


		private static async Task readEmployerCompensation()
		{
			try
			{
				Console.WriteLine("***Started Reading V4 Employer Compensation***");
				var employeeCompensationReadAllQuery = File.ReadAllText("IntuitGraphQL/payroll-read-employerCompensation.graphql");
				JsonValue jsonResponse = await executeIntuitGraphQLRequest(employeeCompensationReadAllQuery, "", true);
				displayEmployerCompensation(jsonResponse);
			}
			catch (Exception ex) { throw ex; }
			finally
			{
				Console.WriteLine("***Finished Reading V4 Employer Compensation***");
			}
		}

		private static async Task<JsonValue> executeIntuitGraphQLRequest(string graphQl, string variables, bool payroll)
		{
			try
			{
				Console.WriteLine("***Started Executing Intuit GraphQL Request***");
				var httpClient = new HttpClient();
				httpClient.DefaultRequestHeaders.Add("Authorization", $"Bearer {getIntuitBearerToken()}");
				if (payroll) { httpClient.DefaultRequestHeaders.Add("provider_override_scheme", "payroll"); }
				var content = $"{{\"query\":{encodeGraphPayload(graphQl)},\"variables\":{encodeGraphPayload(variables)}}}";
				var httpContent = new StringContent(content, Encoding.UTF8, "application/json");
				var httpResponse = await httpClient.PostAsync(intuitGraphQlEndpoint, httpContent);
				if (httpResponse.StatusCode != System.Net.HttpStatusCode.OK) { throw new Exception("Non-200 status code returned from API call"); }
				var httpResponseContent = await httpResponse.Content.ReadAsStringAsync();
				var jsonResponse = JsonValue.Parse(httpResponseContent);
				if (!jsonResponse.ContainsKey("data") || jsonResponse.ContainsKey("errors")) { throw new Exception("Error returned in JSON response"); }
				return jsonResponse;
			}
			catch (Exception ex)
			{
				throw new Exception($"Error calling Intuit GraphQL API: {ex.Message}");
			}
			finally
			{
				Console.WriteLine("***Finished Executing Intuit GraphQL Request***");
			}
		}

		private static void displayEmployeeCompensation(JsonValue employeeCompensation)
		{
			foreach (JsonObject employeeEdge in employeeCompensation["data"]["company"]["employeeContacts"]["edges"])
			{
				JsonValue employeeNode = employeeEdge["node"];
				var employeeId = (string)employeeNode["id"];
				foreach (JsonObject compensationEdge in employeeNode["profiles"]["employee"]["compensations"]["edges"])
				{
					JsonValue compensationNode = compensationEdge["node"];
					var compensationId = (string)compensationNode["id"];
					var compensationName = (string)compensationNode["employerCompensation"]["name"];
					var compensationAmount = compensationNode["amount"] == null ? 0 : Double.Parse((string)compensationNode["amount"]);
					Console.WriteLine($"Employee with ID {employeeId} has the compensation {compensationName} with the ID {compensationId} and current amount of ${compensationAmount}");
				}
			}
			Console.Write("");
		}

		private static void displayEmployerCompensation(JsonValue employerCompensation)
		{
			foreach (JsonObject employeerCompensationEdge in employerCompensation["data"]["company"]["companyInfo"]["employerInfo"]["compensations"]["edges"])
			{
				JsonValue employeerCompensationNode = employeerCompensationEdge["node"];
				var compensationId = (string)employeerCompensationNode["id"];
				var compensationName = (string)employeerCompensationNode["name"];
				var compensationPolicy = (string)employeerCompensationNode["statutoryCompensationPolicy"];
				Console.WriteLine($"Employer has compensation {compensationName} with ID {compensationId} and policy {compensationPolicy}");
			}
            Console.Write("");
		}

		private static string getIntuitBearerToken()
		{
			return File.ReadAllText("Auth/danger-do-not-use.txt"); ;
		}


		static void Main()
		{
			MainAsync().Wait();
		}

		private static string encodeGraphPayload(string payload)
		{
			return JsonConvert.ToString(payload);
		}

	}
}