'use strict';

angular.module('reachoutApp')
    .controller('ProviderController', function ($scope, $http, $state, Provider) {
        $scope.providers = [];
        $scope.search = "";
        $scope.providers = "";
        $scope.filterTerm= ""; 
        $scope.loadAll = function() {
        	Provider.query(function(result) {
               $scope.providers = result;
            });
        };
        $scope.loadAll();

              $scope.active = function(id,active){
                  var url = "api/providers/userActivation"
                      
                  active = !active
                  $scope.data = {
                          id: id,
                          active: active
                  };
                   $http.post(url, $scope.data).success(function(data){
                       $scope.providers = data;
                       $scope.loadAll();
                   });
              };
       
        $scope.refresh = function () {
            $scope.loadAll();
            $scope.clear();
        };

        $scope.itemsPerPage = 20;
        $scope.currentPage = 0;
        $scope.range = function() {
            var rangeSize = 5;
            var ps = [];
            var start;

            start = $scope.currentPage;
            if ( start > $scope.pageCount()-rangeSize ) {
              start = $scope.pageCount()-rangeSize+1;
            }

            for (var i=start; i<start+rangeSize; i++) {
            	 if(i>=0) {
           		  ps.push(i);
         	    }
            }
            return ps;
          };

          $scope.prevPage = function() {
            if ($scope.currentPage > 0) {
              $scope.currentPage--;
            }
          };

          $scope.DisablePrevPage = function() {
            return $scope.currentPage === 0 ? "disabled" : "";
          };

          $scope.pageCount = function() {
            return Math.ceil($scope.providers.length/$scope.itemsPerPage)-1;
          };

          $scope.nextPage = function() {
            if ($scope.currentPage < $scope.pageCount()) {
              $scope.currentPage++;
            }
          };

          $scope.DisableNextPage = function() {
            return $scope.currentPage === $scope.pageCount() ? "disabled" : "";
          };

          $scope.setPage = function(n) {
            $scope.currentPage = n;
          };
               
        
        $scope.clear = function () {
            $scope.provider = {
                mobile: null,
                email: null,
                name: null,
                address: null,
                latitude: null,
                longitude: null,
                url: null,
                consumer_id: null,
                id: null
            };
        };
    }).filter('offset', function() {
     	 return function(input, start) {
       	   start = parseInt(start, 10);
       	   return input.slice(start);
       	 };
       	});
angular.module('reachoutApp').filter('providerFilter', [function () {
    return function (provider, selectedProvider) {
        if (!angular.isUndefined(providers) && !angular.isUndefined(selectedProviders) && selectedProviders.length > 0) {
            var tempProviders = [];
            angular.forEach(selectedProviders, function (name) {
                angular.forEach(providers, function (provider) {
                    if (angular.equals(provider.mobile, name)) {
                    	tempProviders.push(provider);
                    }
                });
            });
            return tempProviders;
        } else {
            return providers;
        }
    };
}]);