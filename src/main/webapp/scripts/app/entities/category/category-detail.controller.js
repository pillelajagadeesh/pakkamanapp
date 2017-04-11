(function() {
    'use strict';

    angular
        .module('reachoutApp')
        .controller('CategoryDetailController', CategoryDetailController);

    CategoryDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Category'];

    function CategoryDetailController($scope, $rootScope, $stateParams, entity, Category) {
        var vm = this;

        vm.category = entity.categoryDetails;
        $scope.subCategoryName=[];
        var unsubscribe = $rootScope.$on('reachoutApp:categoryUpdate', function(event, result) {
            vm.category = result;
            
        });
        angular.forEach(vm.category.category, function(value, key){
                $scope.subCategoryName.push(value.name);
         });
        $scope.$on('$destroy', unsubscribe);
    }
})();
