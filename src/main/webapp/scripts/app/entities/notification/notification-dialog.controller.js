'use strict';

angular.module('reachoutApp').controller('NotificationDialogController',
    ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'Notification','$http','$modal',
        function($scope, $stateParams, $uibModalInstance, entity, Notification,$http,$modal) {

        $scope.notification = entity;
        $scope.notification.radius="000";
        $http.get("api/categories")
        .then(function(response)
        		{ 
        	$scope.details = response.data;
        	}
        );
        $scope.load = function(id) {
            Notification.get({id : id}, function(result) {
                $scope.notification = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('reachoutApp:notificationUpdate', result);
            $uibModalInstance.close(result);
            $scope.isSaving = false;
        };

        var onSaveError = function (result) {
            $scope.isSaving = false;
        };

        $scope.save = function () {
            $scope.isSaving = true;
            if ($scope.notification.id != null) {
                Notification.update($scope.notification, onSaveSuccess, onSaveError);
            } else {
                $http.get("api/getCredit/"+$scope.notification.consumerId)
                .then(function(response)
                        {
                    $scope.freeCredits = response.data[0].free_credit;
                    $scope.walletCredits = response.data[0].wallet_credit;
                    if($scope.freeCredits =="0" && $scope.walletCredits=="0"){
                         $modal.open({
                             templateUrl:"scripts/app/entities/notification/notification-credit.html",
                             controller:[
                                 '$scope', '$modalInstance', function($scope, $modalInstance) {
                                     $scope.popup = function() {
                                         $modalInstance.dismiss();
                                          $uibModalInstance.dismiss('cancel');
                                     };
                                 }
                             ]
                         });
                    }
                    else{
                    	 Notification.save($scope.notification, onSaveSuccess, onSaveError);
                    }
                    }
                );
               
            }
        };

        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.datePickerForValidFrom = {};

        $scope.datePickerForValidFrom.status = {
            opened: false
        };

        $scope.datePickerForValidFromOpen = function($event) {
            $scope.datePickerForValidFrom.status.opened = true;
        };
        $scope.datePickerForValidTo = {};

        $scope.datePickerForValidTo.status = {
            opened: false
        };

        $scope.datePickerForValidToOpen = function($event) {
            $scope.datePickerForValidTo.status.opened = true;
        };
        $scope.datePickerForExpieryDate = {};

        $scope.datePickerForExpieryDate.status = {
            opened: false
        };

        $scope.datePickerForExpieryDateOpen = function($event) {
            $scope.datePickerForExpieryDate.status.opened = true;
        };
        $scope.datePickerForCreated = {};

        $scope.datePickerForCreated.status = {
            opened: false
        };

        $scope.datePickerForCreatedOpen = function($event) {
            $scope.datePickerForCreated.status.opened = true;
        };
        $scope.datePickerForUpdated = {};

        $scope.datePickerForUpdated.status = {
            opened: false
        };

        $scope.datePickerForUpdatedOpen = function($event) {
            $scope.datePickerForUpdated.status.opened = true;
        };
        
        $scope.selectedItemChanged = function(){
        	   console.log($scope.selectedItem);
        		   $http.get("api/categories/"+$scope.selectedItem)
        	        .then(function(response)
        	        		{ 
        	        	$scope.details1 = response.data;
        	        	angular.forEach($scope.details1, function(item){
        	        	    $scope.Id = item.id;
        	        	    $scope.Name = item.name;
        	        	    $scope.CategoryDetails = item.category;
        	        	  });
        	        	}
        	        );
        	  }
}]);
