'use strict';

angular.module('reachoutApp')
    .controller('NotificationAcknowledgementController', function ($scope, $state, NotificationAcknowledgement) {

        $scope.notificationAcknowledgements = [];
        $scope.filterTerm= ""; 
        $scope.sortType = "active";
        $scope.sortReverse  = true;
        $scope.loadAll = function() {
            NotificationAcknowledgement.query(function(result) {
               $scope.notificationAcknowledgements = result;
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
            return Math.ceil($scope.notificationAcknowledgements.length/$scope.itemsPerPage)-1;
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
            $scope.notificationAcknowledgement = {
                read: null,
                consumerId: null,
                notificationId: null,
                delivered: null,
                id: null
            };
        };
    }).filter('offset', function() {
     	 return function(input, start) {
       	   start = parseInt(start, 10);
       	   return input.slice(start);
       	 };
       	});
