DROP DATABASE IF EXISTS markets;
DROP ROLE IF EXISTS musician;

CREATE DATABASE markets;

\connect markets;

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

DROP SCHEMA IF EXISTS public;

CREATE ROLE musician WITH LOGIN ENCRYPTED PASSWORD 'musician_pwd';

CREATE SCHEMA musician AUTHORIZATION musician;