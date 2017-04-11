'use strict';

angular.module('reachoutApp')
    .controller('MainController', function ($scope,$rootScope, Principal) {
        Principal.identity().then(function(account) {
            $scope.account = account;
           $rootScope.loginName= $scope.account.login;
            $scope.isAuthenticated = Principal.isAuthenticated;
        });
    });
