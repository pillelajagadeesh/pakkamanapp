'use strict';

	angular.module('reachoutApp')
    	.controller('NotificationListController', function ($scope, $state,$stateParams, Notification) {
    		 $scope.notifications = [];
    		 $scope.notification ="";
    		  $scope.loadAll = function(id) {
    	            Notification.getConsumerNotifications({ id: $stateParams.id},function(result) {
    	            	 $scope.notifications = result;
    	            });
    	        };
    	        $scope.loadAll();
        
             $scope.refresh = function () {
             	
                 $scope.loadAll();
                 $scope.clear();
             };
        $scope.clear = function () {
            $scope.notification = {
                category: null,
                title: null,
                description: null,
                validFrom: null,
                validTo: null,
                secureUrl: null,
                expieryDate: null,
                delivered: null,
                active: null,
                created: null,
                updated: null,
                offensive: null,
                publicId: null,
                url: null,
                consumerId: null,
                id: null
            };
        };
    });
	
