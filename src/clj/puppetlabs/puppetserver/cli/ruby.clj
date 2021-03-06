(ns puppetlabs.puppetserver.cli.ruby
  (:import (org.jruby Main RubyInstanceConfig CompatVersion)
           (java.util HashMap))
  (:require [puppetlabs.puppetserver.cli.subcommand :as cli]
            [puppetlabs.services.jruby.jruby-puppet-core :as jruby-puppet]))

(defn new-jruby-main
  [config]
  (let [load-path    (->> (get-in config [:os-settings :ruby-load-path])
                          (cons jruby-puppet/ruby-code-dir))
        gem-home     (get-in config [:jruby-puppet :gem-home])
        jruby-config (new RubyInstanceConfig)
        env          (doto (HashMap. (.getEnvironment jruby-config))
                       (.put "GEM_HOME" gem-home))]
    (doto jruby-config
      (.setEnvironment env)
      (.setLoadPaths load-path)
      (.setCompatVersion (CompatVersion/RUBY1_9)))
    (Main. jruby-config)))

(defn run!
  [config ruby-args]
  (-> (new-jruby-main config)
    (.run (into-array String ruby-args))))

(defn -main
  [& args]
  (cli/run run! args))
