<?php

require "Client.php";

$client = new Client([
    'ClientID' => 'add your client id',
    'ClientSecret' => 'add your client secret',
    'BaseUrl' => 'https://v4thirdparty.api.intuit.com/',
    'AccessToken' => 'add your oauth2 access token'
]);


//heredoc
$query = <<<'QUERY'
query compensationQuery {
   company {
     companyInfo {
        employerInfo {
          compensations {
              edges {
                node {
                  name
                  id
                  statutoryCompensationPolicy
                }
              }
          }
        }
     }
  }
}
QUERY;

$variable = [];
$response = $client->query($query, $variable);
$edges= $response['data']['company']['companyInfo']['employerInfo']['compensations']['edges'];
foreach ($edges as $edge){
    $node = $edge['node'];
    print("The node is: {" . $node['id'] . " " . $node['name'] . " " . $node['statutoryCompensationPolicy'] . "}\n");
}


?>
