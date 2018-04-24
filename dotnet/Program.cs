using System;
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
            const string graphQlBaseUrl = "https://v4thirdparty.api.intuit.com/graphql";
            client.DefaultRequestHeaders.Add("provider_override_scheme", "payroll");
            client.DefaultRequestHeaders.Add("Authorization", $"Bearer {accessToken}");

            //Employee Compensation
            const string employeeCompensationQuery = "{\"query\":\"query {\\n  company {\\n    employeeContacts {\\n      edges {\\n        node {\\n          id\\n          person {\\n            givenName\\n          }\\n          profiles {\\n            employee {\\n              compensations {\\n                edges {\\n                  node {\\n                    id\\n                    amount\\n                    employerCompensation {\\n                      name\\n                    }\\n                  }\\n                }\\n              }\\n            }\\n          }\\n        }\\n      }\\n    }\\n  }\\n} \"}";
            StringContent queryString = new StringContent(employeeCompensationQuery, Encoding.UTF8, "application/json");
            var response = await client.PostAsync(graphQlBaseUrl, queryString);
            var responseContent = await response.Content.ReadAsStringAsync();
            Console.WriteLine($"Employee Compensation Response: {responseContent}");

            //Employer Compensation
            const string employerCompensationQuery = "{\"query\":\"query {\\n company {\\n companyInfo {\\n employerInfo {\\n compensations {\\n edges {\\n node {\\n id\\n name\\n statutoryCompensationPolicy\\n            }\\n          }\\n        }\\n      }\\n    }\\n  }\\n} \",\"variables\":null}";
            queryString = new StringContent(employerCompensationQuery, Encoding.UTF8, "application/json");
            response = await client.PostAsync(graphQlBaseUrl, queryString);
            responseContent = await response.Content.ReadAsStringAsync();
            Console.WriteLine($"Employer Compensation Response: {responseContent}");
        }
    }
}