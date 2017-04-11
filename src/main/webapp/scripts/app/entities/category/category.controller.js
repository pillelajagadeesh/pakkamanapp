(function() {
    'use strict';

    angular
        .module('reachoutApp')
        .controller('CategoryController', CategoryController);

    CategoryController.$inject = ['$scope', '$state', 'Category','$http'];

    function CategoryController ($scope, $state, Category, $http) {
        var vm = this;
        vm.categories = [];

        /*loadAll();

        function loadAll() {
            Category.query(function(result) {
                vm.categories = result;
            });
        }*/
        
        loadAll();
        
        function loadAll() {
        var url = "api/adminCategories"
        
        $http.get(url).success(function(result){
            
        	vm.categories = result;
             
               });
        }
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
            return Math.ceil(vm.categories.length/$scope.itemsPerPage)-1;
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
               
    }
})().filter('offset', function() {
 	 return function(input, start) {
   	   start = parseInt(start, 10);
   	   return input.slice(start);
   	 };
   });
