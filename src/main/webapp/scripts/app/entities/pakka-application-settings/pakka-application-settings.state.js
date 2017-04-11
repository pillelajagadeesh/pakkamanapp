(function() {
    'use strict';

    angular
        .module('reachoutApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('pakka-application-settings', {
            parent: 'entity',
            url: '/pakka-application-settings',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'reachoutApp.pakkaApplicationSettings.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'scripts/app/entities/pakka-application-settings/pakka-application-settings.html',
                    controller: 'PakkaApplicationSettingsController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('pakkaApplicationSettings');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('pakka-application-settings-detail', {
            parent: 'entity',
            url: '/pakka-application-settings/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'reachoutApp.pakkaApplicationSettings.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'scripts/app/entities/pakka-application-settings/pakka-application-settings-detail.html',
                    controller: 'PakkaApplicationSettingsDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('pakkaApplicationSettings');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'PakkaApplicationSettings', function($stateParams, PakkaApplicationSettings) {
                    return PakkaApplicationSettings.get({id : $stateParams.id}).$promise;
                }]
            }
        })
        .state('pakka-application-settings.new', {
            parent: 'pakka-application-settings',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'scripts/app/entities/pakka-application-settings/pakka-application-settings-dialog.html',
                    controller: 'PakkaApplicationSettingsDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                description: null,
                                value: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('pakka-application-settings', null, { reload: true });
                }, function() {
                    $state.go('pakka-application-settings');
                });
            }]
        })
        .state('pakka-application-settings.edit', {
            parent: 'pakka-application-settings',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'scripts/app/entities/pakka-application-settings/pakka-application-settings-dialog.html',
                    controller: 'PakkaApplicationSettingsDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['PakkaApplicationSettings', function(PakkaApplicationSettings) {
                            return PakkaApplicationSettings.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('pakka-application-settings', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('pakka-application-settings.delete', {
            parent: 'pakka-application-settings',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'scripts/app/entities/pakka-application-settings/pakka-application-settings-delete-dialog.html',
                    controller: 'PakkaApplicationSettingsDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['PakkaApplicationSettings', function(PakkaApplicationSettings) {
                            return PakkaApplicationSettings.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('pakka-application-settings', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
