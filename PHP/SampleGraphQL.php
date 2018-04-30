<?php

require "Client.php";

$client = new Client([
    'ClientID' => 'Q3MyeJDoYLkiUNt1ujVd4rKA3bVUuBJpmos19WiiSgT5jBJ9ct',
    'ClientSecret' => '84wfiMiOYesBYxkYjMhtntAB2TpFuRLnlgicxUWw',
    'BaseUrl' => 'https://v4thirdparty-e2e.api.intuit.com/',
    'AccessToken' => 'eyJlbmMiOiJBMTI4Q0JDLUhTMjU2IiwiYWxnIjoiZGlyIn0..2mPj9rG57Q_RWv0Ta_ozxg.KXepMlGnBdq6yncKrwJfTy_XZRHD7vjemoAaBsieqMWg68-P9VH65qxOJ-C3CGhfpNbto_SNSS-YOCqtB9NWV1ZGDYPzAZrycXin-tMy9lJPncSti9vp5qNIj9KDkHvorC0UKdLOxr95wDHV4sZenkR5IPfvoA0sCvHWk16KCtJGlcOm-xCREqISkyuD4M4Jpsc2tJlRRNFDdMzmojkOyYe2h9Cb8PBPqSajS8OEYxF1zxtiucjLVKacGQheNnfF3PvGJIcnltOARLW6ny-o4EM3Yx6HRQeJEGR0MJYBcngd-ctQ1JUMJq2mJSDzSDCg0ytNhjgLfYxc73gsnkU0mkDiSgF8FudruahOGWIk-hKm4aPcHQjD7sw7RCocdsP4Xt3hp2YB-Huw7eqw85kb9E4MnxHSVIv2Zqlk5paL-teI6ui8Sa47LHesbi8TozAUaATe0h9hfxGTNOlFeWP3vSXPANv58ZNtccGJasYc-I6Po2bhry6i8TxiU6hIraBW-8o-fYK_BXNqHaRvKp4HhMZ8_7Zk9B0Ehv78siO_JA9WkANvhIB0rZwBsI9iPM8w54QrwB5WPYMZZzlgYfKuRYuReQTd4GV9kfcGtPk44xQtJgPiYukUYn3X3-p5mrLxXvu9xTiLdPVsn49JF5RUgQ7qxcG8zNWb1QJT7k0bKXADcx60bsD_JMNXFNEwBmWk._Uz37IvBUMEExJfazpWNkA'
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
