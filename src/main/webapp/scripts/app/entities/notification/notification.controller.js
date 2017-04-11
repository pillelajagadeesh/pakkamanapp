'use strict';

	angular.module('reachoutApp')
		.filter('distance', function() {
			return function(input) {
				if (input >= 1000) {
					/*return (input / 1000).toFixed(2) + 'km';*/
					return (input / 1000);
				} else {
					return input;
				}
			}
		})
    	.controller('NotificationController', function ($scope,$http, $state,$location,$filter,$rootScope,$cookies,$timeout, Notification, distanceFilter,$modal,$window) {
        $scope.notifications = [];
        $scope.sortType = "active";
        $scope.sortReverse  = true;
        $scope.search = "";
        $scope.notification = {};
        $scope.filterTerm= ""; 
        
        $http.get("api/categories")
        .then(function(response)
        		{ 
        	$scope.details = response.data;
        	}
        );
        $http.get("api/subCategories")
        .then(function(response)
        		{ 
        	$scope.subCategoriesDetails = response.data;
        	}
        );
        
        $scope.loadAll = function(status,category,mainCategory) {
        	category = category==undefined ? '' : category;
        	mainCategory= mainCategory== undefined ? '' : mainCategory;
        	if(status == null || status==undefined){
        		
        		status = false;
        	}
            Notification.query({category:category,status:status,mainCategory:mainCategory},function(result) {
               $scope.myInterval = 3000;
               $scope.notifications = result;
            });
        };
        $scope.loadAll($scope.notification.active,$scope.notification.category,$scope.notification.mainCategory);
        
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
            return Math.ceil($scope.notifications.length/$scope.itemsPerPage)-1;
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
                 
        //}

        /*
         * Done by jitesh for Filtering
         */
        
        
       
        var approvedBy = JSON.parse($cookies.get('approved'));
        
        $scope.approve = function(id,active){
        	var url = "api/notifications/approve"
        	// what is the seklected value in dropdown
        	var category = $("#category-"+id+" option:selected").val();
        	var subCategory = $("#subCategory-"+id+" option:selected").val();
        	active = !active
        	$scope.data = {
        			id: id,
        			active: active,
        			category: category,
        			subCategory: subCategory,
        			approvedBy: approvedBy
        	};
        	 
        	 $http.post(url, $scope.data).success(function(data){
        		 $scope.notifications = data;
        		 $scope.loadAll($scope.notification.active,$scope.notification.category);
        	 });
        	
        };
        /**
         * handelling the drop down change event we have to filter category & active from back end 
         * done by Rithuik on 28th June,2016
         */
        
        //function to handle the category filter
        $scope.changedCategory = function()
        {
        	var selectedCategory  = $scope.notification.category == undefined ? '' : $scope.notification.category;
        	var selectedMainCategory  = $scope.notification.mainCategory == undefined ? '' : $scope.notification.mainCategory;
        	if(selectedMainCategory != "" && selectedCategory != ""){
        	        $modal.open({
        	            templateUrl:"scripts/app/entities/notification/notification-pop.html",
        	            controller:[
        	                '$scope', '$modalInstance', function($scope, $modalInstance) {
        	                	$scope.popup = function() {
        	                		$modalInstance.dismiss();
        	                		$window.location.reload();
        	                    };
        	                }
        	            ]
        	        });
        }
        	var selectedStatus  = $scope.notify ==undefined ? false : $scope.notify;
        	$scope.loadAll(selectedStatus,selectedCategory,selectedMainCategory);
        }
        
        $scope.changedStatus = function()
        {
        	var selectedCategory  = $scope.notification.category == undefined ? '' : $scope.notification.category;
        	var selectedMainCategory  = $scope.notification.mainCategory == undefined ? '' : $scope.notification.mainCategory;
        	var selectedStatus  = $scope.notify ==undefined ? false : $scope.notify;
        	$scope.loadAll(selectedStatus,selectedCategory,selectedMainCategory);
        };
        
        $scope.refresh = function () {
        	$scope.loadAll($scope.notification.active,$scope.notification.category,$scope.notification.mainCategory);
            $scope.clear();
        };
        
        $scope.suspend = function(id,id1){
        	
        	var url = "api/notifications/suspend"
        	// what is the seklected value in dropdow
        	alert("Are you sure you want to delete this Notification and block the consumer:"+id);
        	$scope.data = {
        			id: id,
        			id1:id1
        	};
        	 $http.post(url, $scope.data).success(function(data){
        		 $scope.consumers = data;
        		 $scope.notifications = data;
        		 $scope.loadAll($scope.notification.active,$scope.notification.category);
        	 });
        	 

        };
        
        $scope.refresh = function () {
        	
        	$scope.loadAll($scope.notification.active,$scope.notification.category);
            $scope.clear();
        };
		/* Done by jitesh*/
        $scope.selectedNotifications = [];
        $scope.categoryList = [{
            name: 'Food'
        }, {
            name: 'Travel'
        }, {
            name: 'Electronics'
        }];
        
        $scope.setselectedNotification = function () {
            var id = this.category.name;
            if (_.contains($scope.selectedNotifications, id)) {
                $scope.selectedNotifications = _.without($scope.selectedNotifications, id);
            } else {
                $scope.selectedNotifications.push(id);
            }
            return false;
        };

        $scope.isChecked = function (name) {
            if (_.contains($scope.selectedNotifications, name)) {
                return 'glyphicon glyphicon-ok pull-right';
            }
            return false;
        };

        $scope.checkAll = function () {
            $scope.selectedNotifications = _.pluck($scope.categoryList, 'name');
        };
      
    
        $scope.selectedItemChanged = function(){
     	   console.log($scope.selectedItem);
     	  if($scope.selectedItem !="" &&$scope.selectedItem !=null ){
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
     	  else{
     		$state.go($state.current, {}, {reload: true});
     	  }
     	  }
        $scope.clear = function () {
            $scope.notification = {
                category: null,
                title: null,
                description: null,
                validFrom: null,
                validTo: null,
                secureUrl: null,
                mainCategoryId:null,
                expieryDate: null,
                delivered: null,
                active: null,
                created: null,
                updated: null,
                deleted:null,
                offensive: null,
                publicId: null,
                url: null,
                consumerId: null,
                id: null
            };
        };
    }).filter('offset', function() {
      	 return function(input, start) {
         	   start = parseInt(start, 10);
         	   return input.slice(start);
         	 };
         	});
	
	angular.module('reachoutApp').filter('notificationFilter', [function () {
		   return function (notifications, selectedNotifications) {
		       if (!angular.isUndefined(notifications) && !angular.isUndefined(selectedNotifications) && selectedNotifications.length > 0) 
		       {
		           var tempNotifications = [];
		           angular.forEach(selectedNotifications, function (name,heading,text,date,date1,offen) {
		               angular.forEach(notifications, function (notification) 
		               {
		                    if (angular.equals(notification.category, name)&& angular.equals(notification.title, heading)&& angular.equals(notification.description, text)&& angular.equals(notification.validFrom, date)&& angular.equals(notification.validTo, date1)&& angular.equals(notification.offensive, offen)) 
		                    {
		                     	tempNotifications.push(notification);
		                    }
		               });
		           });
		           return tempNotifications;
		       } else {
		           return notifications;
		       }
		   };
     }]);

	
	