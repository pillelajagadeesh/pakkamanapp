'use strict';

angular.module('reachoutApp')
    .controller('ContentMetadataController', function ($scope, $state, ContentMetadata) {
        $scope.contentmetadatas = [];
        $scope.sortType = "active";
        $scope.sortReverse  = true;
        $scope.filterTerm= "";
        $scope.loadAll = function() {
        	ContentMetadata.query(function(result) {
               $scope.contentmetadatas = result;
            });
        };
        $scope.loadAll();


        $scope.refresh = function () {
            $scope.loadAll();
            $scope.clear();
        };
        $scope.itemsPerPage = 20;
        $scope.currentPage = 0;

     

        $scope.range = function() {
          var rangeSize = 5;
          var ret = [];
          var start=0;

        /*  start = $scope.currentPage;*/
          /*if ( start > rangeSize-$scope.pageCount() ) {
            start = rangeSize-$scope.pageCount()+1;
          }*/
          for (var i=start; i<$scope.pageCount(); i++) {
        	  
              ret.push(i);
            }
            return ret;
        };

        $scope.prevPage = function() {
          if ($scope.currentPage > 0) {
            $scope.currentPage--;
          }
        };

        $scope.prevPageDisabled = function() {
          return $scope.currentPage === 0 ? "disabled" : "";
        };

        $scope.pageCount = function() {
          return Math.ceil($scope.contentmetadatas.length/$scope.itemsPerPage)
        };

        $scope.nextPage = function() {
          if ($scope.currentPage < $scope.pageCount()-1) {
            $scope.currentPage++;
          }
        };

        $scope.nextPageDisabled = function() {
          return $scope.currentPage === $scope.pageCount()-1 ? "disabled" : "";
        };

        $scope.setPage = function(n) {
          $scope.currentPage = n;
        };
        $scope.clear = function () {
            $scope.contentMetadata = {
                consumerId: null,
                signature: null,
                format: null,
                resourceType: null,
                secureUrl: null,
                created: null,
                type: null,
                version: null,
                url: null,
                publicId: null,
                tags: null,
                orginalFileName: null,
                bytes: null,
                width: null,
                eTag: null,
                height: null,
                id: null
            };
        };
    }).filter('offset', function() {
    	 return function(input, start) {
         	   start = parseInt(start, 10);
         	   return input.slice(start);
         	 };
         	});
