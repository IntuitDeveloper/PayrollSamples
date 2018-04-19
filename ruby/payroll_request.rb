require 'httpclient'
require 'json'

# Add OAuth2 access_token here
access_token = ''

# Set up base url, headers and HTTP Client
url = 'https://v4thirdparty.api.intuit.com/graphql'
headers = {'Authorization' => 'Bearer ' + access_token, 'Accept' => 'application/json', 'Content-Type' => 'application/json; version=1.6.91; charset=utf-8', 'provider_override_scheme' => 'payroll'}
client = HTTPClient.new

=begin
Graph Query for getting all Employer Compensation
=end
employer_graph_query = <<-'GRAPHQL'
query{
  company{
    companyInfo{
      employerInfo{
        compensations{
          edges{
            node{
              id
              name
              statutoryCompensationPolicy
            }
          }
        }
      }
    }
  }
}
GRAPHQL

payload = { "query" => employer_graph_query, "variables" => nil }
response = client.post url, payload.to_json, headers

# Parse API response
begin
  puts "\n" + 'Employer compensation response' 
  res = JSON.parse response.body
  edges = res['data']['company']['companyInfo']['employerInfo']['compensations']['edges']
  node = 0
  while node < edges.length do 
    puts 'Name: ' + edges[node]['node']['name'] + '; Id: ' + edges[node]['node']['id']
    node += 1
  end
rescue
  puts response.code
  puts response.body
end

=begin
Graph Query for getting Employee Compensation
=end
employee_graph_query = <<-'GRAPHQL'
query {
  company {
    employeeContacts {
      edges {
        node {
          id
          person {
            givenName
          }
          profiles {
            employee {
              compensations(filterBy: "endDate = null") {
                edges {
                  node {
                    id
                    amount
                    endDate
                    employerCompensation {
                      name
                      id
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
GRAPHQL
payload = { "query" => employee_graph_query, "variables" => nil }
response = client.post url, payload.to_json, headers

# Parse API response
begin
  puts "\n\n" + 'Employee compensation response' 
  res = JSON.parse response.body
  employee_edge = res['data']['company']['employeeContacts']['edges']
  node = 0
  while node < employee_edge.length do 
    puts 'For Employee Name: ' + employee_edge[node]['node']['person']['givenName'] + '; Employee Id: ' + employee_edge[node]['node']['id'] + ' compensations are: ' 
    comp_node = 0
    compensation_edge = employee_edge[node]['node']['profiles']['employee']['compensations']['edges']
    while comp_node < compensation_edge.length do
      puts "\t" + 'Employer Compensation name: ' + compensation_edge[comp_node]['node']['employerCompensation']['name'] + ' ; Employer Compensation Id: ' + compensation_edge[comp_node]['node']['employerCompensation']['id'] + '; Employee Compensation Id: ' + compensation_edge[comp_node]['node']['id'] + '; Employee Compensation amount: ' + compensation_edge[comp_node]['node']['amount']
      comp_node += 1
    end
    node += 1
  end
rescue
  puts response.code
  puts response.body
end
