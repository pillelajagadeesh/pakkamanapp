(function() {
    'use strict';

    angular
        .module('reachoutApp')
        .controller('CategoryDialogController', CategoryDialogController);

    CategoryDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Category'];

    function CategoryDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Category) {
        var vm = this;

        vm.category = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }
        
        function save () {
            vm.isSaving = true;
            if (vm.category.id !== null) {
                Category.update(vm.category.categoryDetails, onSaveSuccess, onSaveError);
            } else {
                Category.save(vm.category.categoryDetails, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('reachoutApp:categoryUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.created = false;
        vm.datePickerOpenStatus.deleted = false;
        vm.datePickerOpenStatus.lastUpdate = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
