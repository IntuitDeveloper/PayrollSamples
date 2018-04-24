using System;
using System.Collections.Generic;
using System.Json;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;

namespace NetCoreV4Samples
{
    class Program
    {
        static void Main()
        {
            MainAsync().Wait();
        }

        public static async Task MainAsync()
        {
            HttpClient client = new HttpClient();
            const string accessToken = "add oauth2 access token";
            const string graphQlUrl = "https://v4thirdparty.api.intuit.com/graphql";
            client.DefaultRequestHeaders.Add("provider_override_scheme", "payroll");
            client.DefaultRequestHeaders.Add("Authorization", $"Bearer {accessToken}");
            await getEmployeeCompensation(client, graphQlUrl);
            await getEmployerCompensation(client, graphQlUrl);
        }

        private static async Task getEmployeeCompensation(HttpClient httpClient, String graphQlUrl)
        {
            try
            {
                Console.WriteLine("***Performing V4 Employee Compensation Query***");
                const string employeeCompensationQuery = "{\"query\":\"query {\\n  company {\\n    employeeContacts {\\n      edges {\\n        node {\\n          id\\n          person {\\n            givenName\\n          }\\n          profiles {\\n            employee {\\n              compensations {\\n                edges {\\n                  node {\\n                    id\\n                    amount\\n                    employerCompensation {\\n                      name\\n                    }\\n                  }\\n                }\\n              }\\n            }\\n          }\\n        }\\n      }\\n    }\\n  }\\n} \"}";
                StringContent queryString = new StringContent(employeeCompensationQuery, Encoding.UTF8, "application/json");
                var response = await httpClient.PostAsync(graphQlUrl, queryString);
                if (response.StatusCode != System.Net.HttpStatusCode.OK) { throw new Exception("Non-200 status code returned from API call"); }
                var responseContent = await response.Content.ReadAsStringAsync();
                JsonValue jsonResponse = JsonValue.Parse(responseContent);
                if (!jsonResponse.ContainsKey("data") || jsonResponse.ContainsKey("errors")) { throw new Exception("Error returned in JSON response"); }
                foreach (JsonObject employeeEdge in jsonResponse["data"]["company"]["employeeContacts"]["edges"])
                {
                    JsonValue employeeNode = employeeEdge["node"];
                    var employeeId = (string)employeeNode["id"];
                    var employeeName = (string)employeeNode["person"]["givenName"];
                    foreach (JsonObject compensationEdge in employeeNode["profiles"]["employee"]["compensations"]["edges"])
                    {
                        JsonValue compensationNode = compensationEdge["node"];
                        var compensationId = (string)compensationNode["id"];
                        var compensationName = (string)compensationNode["employerCompensation"]["name"];
                        var compensationAmount = compensationNode["amount"] == null ? 0 : Double.Parse((string)compensationNode["amount"]);
                        Console.WriteLine($"Employee {employeeName} with ID {employeeId} has the compensation {compensationName} with the ID {compensationId} and current amount of ${compensationAmount}");
                    }
                }
                Console.WriteLine("***V4 Employee Compensation Query Success***");
            }
            catch (Exception ex)
            {
                Console.WriteLine($"***Error calling Employee Compensation API: {ex.Message}***");
            }
            Console.WriteLine("");
        }


        private static async Task getEmployerCompensation(HttpClient httpClient, String graphQlUrl)
        {
            try
            {
                Console.WriteLine("***Performing V4 Employer Compensation Query***");
                const string employerCompensationQuery = "{\"query\":\"query {\\n company {\\n companyInfo {\\n employerInfo {\\n compensations {\\n edges {\\n node {\\n id\\n name\\n statutoryCompensationPolicy\\n            }\\n          }\\n        }\\n      }\\n    }\\n  }\\n} \",\"variables\":null}";
                StringContent queryString = new StringContent(employerCompensationQuery, Encoding.UTF8, "application/json");
                var response = await httpClient.PostAsync(graphQlUrl, queryString);
                if (response.StatusCode != System.Net.HttpStatusCode.OK) { throw new Exception("Non-200 status code returned from API call"); }
                var responseContent = await response.Content.ReadAsStringAsync();
                JsonValue jsonResponse = JsonValue.Parse(responseContent);
                if (!jsonResponse.ContainsKey("data") ||  jsonResponse.ContainsKey("errors")) { throw new Exception("Error returned in JSON response"); }
                foreach (JsonObject employeerCompensationEdge in jsonResponse["data"]["company"]["companyInfo"]["employerInfo"]["compensations"]["edges"])
                {
                    JsonValue employeerCompensationNode = employeerCompensationEdge["node"];
                    var compensationId = (string)employeerCompensationNode["id"];
                    var compensationName = (string)employeerCompensationNode["name"];
                    var compensationPolicy = (string)employeerCompensationNode["statutoryCompensationPolicy"];
                    Console.WriteLine($"Employer has compensation {compensationName} with ID {compensationId} and policy {compensationPolicy}");
                }
                Console.WriteLine("***V4 Employer Compensation Query Success***");
            }
            catch (Exception ex)
            {
                Console.WriteLine($"***Error calling Employer Compensation API: {ex.Message}***");
            }
            Console.WriteLine("");
        }
    }
}