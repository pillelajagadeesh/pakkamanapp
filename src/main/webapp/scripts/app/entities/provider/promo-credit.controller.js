'use strict';

angular.module('reachoutApp')
	.controller('PromoCreditController', function($scope,$http, $stateParams, $uibModalInstance, Provider) {
         
         $scope.providers = [];
         $scope.provider = "";
         
        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.confirmBlock = function(id,promo) {
        	
            var id= $stateParams.id;
        	var promo= $stateParams.promo;
        	if(promo=="true"){
        		
        		promo=false;
        	}
        	else{
        		
        		promo=true;
        	}
           	var url = "api/elegibleforcredit/approve"
           		
           	$scope.data = {
           			id: id,
           			eleigible_for_promo_credit:promo,
           			
           	};
           	 
           	 $http.post(url, $scope.data).success(function(data){
           		 $scope.providers = data;
           		$uibModalInstance.close(true);
                    $scope.loadAll();
           	 });
         };
         
     });
