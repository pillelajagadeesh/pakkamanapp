'use strict';

angular.module('reachoutApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('notification', {
                parent: 'entity',
                url: '/notifications',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'reachoutApp.notification.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/notification/notifications.html',
                        controller: 'NotificationController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('notification');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
              .state('provider.list', {
                parent: 'entity',
                url: '/provider/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'reachoutApp.provider.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/provider/provider-detail.html',
                        controller: 'ProviderDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('provider');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'Provider', function($stateParams, Provider) {
                        return Provider.get({id : $stateParams.id});
                    }]
                }
            })
            .state('notification.detail', {
                parent: 'entity',
                url: '/notification/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'reachoutApp.notification.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/notification/notification-detail.html',
                        controller: 'NotificationDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('notification');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'Notification', function($stateParams, Notification) {
                        return Notification.get({id : $stateParams.id});
                    }]
                }
            })
             .state('notification.list', {
                parent: 'entity',
                url: '/getMyNotifications/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'reachoutApp.notification.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/notification/notification-list.html',
                        controller: 'NotificationListController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('notification');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('notification.new', {
                parent: 'notification',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/notification/notification-dialog.html',
                        controller: 'NotificationDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
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
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('notification', null, { reload: true });
                    }, function() {
                        $state.go('notification');
                    })
                }]
            })
            .state('notification.block', {
               parent: 'notification',
               url: '/{id}/block',
               data: {
                   authorities: ['ROLE_USER'],
               },
               onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                   $uibModal.open({
                       templateUrl: 'scripts/app/entities/notification/notification-block-dialog.html',
                       controller: 'NotificationBlockController',
                       size: 'md',
                       resolve: {
                           translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                               $translatePartialLoader.addPart('notification');
                               $translatePartialLoader.addPart('global');
                               return $translate.refresh();
                           }]
                       }
                   }).result.then(function(result) {
                       $state.go('notification', null, { reload: true });
                   }, function() {
                       $state.go('^');
                   })
               }]
           })
            .state('notification.edit', {
                parent: 'notification',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/notification/notification-dialog.html',
                        controller: 'NotificationDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['Notification', function(Notification) {
                                return Notification.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('notification', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            })
            .state('notification.delete', {
                parent: 'notification',
                url: '/{id}/delete',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/notification/notification-delete-dialog.html',
                        controller: 'NotificationDeleteController',
                        size: 'md',
                        resolve: {
                            entity: ['Notification', function(Notification) {
                                return Notification.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('notification', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
