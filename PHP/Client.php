<?php

require "./OAuth2/OAuth2TokenContainer.php";
require "./HttpClient/CurlClient.php";

class Client{

    private $curlClient = null;
    private $baseURL = null;
    private $oauth2TokenContainer = null;
    private $config = null;

    public function __construct(Array $array){
        $this->setOAuth2Tokens($array);
        $this->baseURL = array_key_exists('BaseUrl', $array) ? $array['BaseUrl'] : "https://v4thirdparty-e2e.api.intuit.com/";
        $this->curlClient = new CurlClient();
        $this->config = $array;
    }

    private function constructAuthorizationHeader(Array $array){
        $authorizationHeader = array_key_exists('Authorization', $array) ? $array['Authorization'] : "Bearer " . $this->oauth2TokenContainer->getAccessToken();
        $https_header = [
            'Accept' => 'Application/json',
            'Content-Type' => 'application/json; version=1.6.93; charset=utf-8',
            'Authorization' => $authorizationHeader,
            'provider_override_scheme' => 'payroll'
        ];
        return $https_header;
    }

    private function setOAuth2Tokens(Array $array){
        $clientID = $array['ClientID'];
        $clientSecret = $array['ClientSecret'];
        $accessToken = $array['AccessToken'];
        $this->oauth2TokenContainer = new OAuth2TokenContainer($clientID, $clientSecret, $accessToken);
    }

    public function query($query, Array $variables = []){
        $this->baseURL = $this->baseURL . "graphql";
        $body = [
            "query" => $query,
            "variables" => empty($variables) ? "" : $variables
        ];

        $headers = $this->constructAuthorizationHeader($this->config);

        try{
           $response = $this->curlClient->makeAPICall($this->baseURL, "POST", $headers, json_encode($body), null, false);
        } catch (Exception $e){
           throw new \Exception($e);
        }

        //var_dump($response);
        return json_decode($response, true);
    }
}

 ?>
