'use strict';

angular.module('reachoutApp')
	.controller('NotificationBlockController', function($scope, $stateParams, $uibModalInstance, Notification) {
         
         $scope.notifications = [];
         $scope.notification = "";
         
        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        /*$scope.confirmDelete = function (id) {
            Notification.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };*/
        $scope.confirmBlock = function(id) {
        	
            Notification.blockNotification({ id: $stateParams.id},
            	 
            	 function () {
                     $uibModalInstance.close(true);
                 });
           
        };
        
    });
