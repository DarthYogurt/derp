from django.conf.urls import patterns, include, url
from django.contrib import admin

from derp_app import * 

urlpatterns = patterns('',
    # Examples:
    # url(r'^$', 'derp.views.home', name='home'),
    # url(r'^blog/', include('blog.urls')),

    url(r'^login/', views.login),
    url(r'^admin/', include(admin.site.urls)),
)
