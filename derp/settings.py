"""
Django settings for derp project.

For more information on this file, see
https://docs.djangoproject.com/en/dev/topics/settings/

For the full list of settings and their values, see
https://docs.djangoproject.com/en/dev/ref/settings/
"""

# Build paths inside the project like this: os.path.join(BASE_DIR, ...)
import os
import socket


BASE_DIR = os.path.dirname(os.path.dirname(__file__))


# Quick-start development settings - unsuitable for production
# See https://docs.djangoproject.com/en/dev/howto/deployment/checklist/

# SECURITY WARNING: keep the secret key used in production secret!
SECRET_KEY = 'wny(3n6k_mc6rn88)!-f5k0a4=arv91vo=5_gl#rlr+c^2c(9m'

# SECURITY WARNING: don't run with debug turned on in production!
DEBUG = True

TEMPLATE_DEBUG = True

ALLOWED_HOSTS = []


# Application definition

INSTALLED_APPS = (
    'django.contrib.admin',
    'django.contrib.auth',
    'django.contrib.contenttypes',
    'django.contrib.sessions',
    'django.contrib.messages',
    'django.contrib.staticfiles',
    'derp_app'
)

MIDDLEWARE_CLASSES = (
    'django.contrib.sessions.middleware.SessionMiddleware',
    'django.middleware.common.CommonMiddleware',
    'django.middleware.csrf.CsrfViewMiddleware',
    'django.contrib.auth.middleware.AuthenticationMiddleware',
    'django.contrib.messages.middleware.MessageMiddleware',
    'django.middleware.clickjacking.XFrameOptionsMiddleware',
)

ROOT_URLCONF = 'derp.urls'

WSGI_APPLICATION = 'derp.wsgi.application'


# Database
# https://docs.djangoproject.com/en/dev/ref/settings/#databases

DATABASES = {}
PROJECT_PATH = None
MEDIA_ROOT = None
if socket.gethostname() != "dev.darthyogurt.com":
    DATABASES = {
                  
         'default': {
                'ENGINE': 'django.db.backends.mysql',
                'NAME': 'derp',
                'USER': 'root',
                'PASSWORD': 'supermanfly',
                'HOST': '127.0.0.1',
                'PORT': '3306',
        }
    }
    
    #image files
   
#     MEDIA_URL = "/media/"
else:
    DATABASES = {
                  
         'default': {
                'ENGINE': 'django.db.backends.mysql',
                'NAME': 'derp',
                'USER': 'root',
                'PASSWORD': 'supermanfly',
                'HOST': 'localhost',
                'PORT': '3306',
        }
    }
    #image files
# Internationalization
# https://docs.djangoproject.com/en/dev/topics/i18n/

LANGUAGE_CODE = 'en-us'

TIME_ZONE = 'UTC'

USE_I18N = True

USE_L10N = True

USE_TZ = True

DEFAULT_CHARSET = 'utf-8'
# Static files (CSS, JavaScript, Images)
# https://docs.djangoproject.com/en/dev/howto/static-files/

STATIC_URL = '/static/'
MEDIA_ROOT = '/derp_media/'
MEDIA_URL = '/derp_media/'
