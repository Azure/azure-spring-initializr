import queryString from 'query-string'
import { toast } from 'react-toastify'
import { useContext, useEffect, useState } from 'react'

import { AppContext } from '../reducer/App'
import { InitializrContext } from '../reducer/Initializr'
import { isValidParams } from './ApiUtils'
import { Button } from '../common/form'
import React from 'react'

const getHash = () => {
  return window.location.hash
}

const clearHash = () => {
  if (window.location.hash) {
    if (window.history.pushState) {
      window.history.pushState(null, null, window.location.pathname)
    } else {
      window.history.hash = ``
    }
  }
}

export default function useHash() {
  const [hash, setHash] = useState(getHash())

  const { dispatch } = useContext(InitializrContext)
  const { config, complete } = useContext(AppContext)

  useEffect(() => {
    const handler = () => {
      setHash(getHash())
    }
    window.addEventListener('hashchange', handler)
    return () => {
      window.removeEventListener('hashchange', handler)
    }
  }, [])

  useEffect(() => {
    if (complete && hash) {
      const params = queryString.parse(`?${hash.substr(2)}`)
      dispatch({ type: 'LOAD', payload: { params, lists: config.lists } })
      clearHash()
      setHash('')
      if (isValidParams(params)) {
        const code = params['?errorcode'] || params.errorcode || null;
        if (code === null) {
          toast.success(`Configuration loaded.`, { autoClose: 1500 });
        } else if (code === '200' || code === '0') {
          function copy() {
            let textareaEl = document.createElement('textarea');
            textareaEl.setAttribute('readonly', 'readonly');
            textareaEl.value = `git clone ${params.msg}`;
            document.body.appendChild(textareaEl);
            textareaEl.select();
            let isSuccess = document.execCommand('copy');
            document.body.removeChild(textareaEl);
            return isSuccess;
          }
          function redirect() {
            const rurl = params.msg.replace(/\.git$/, '');
            window.open(rurl);
          }
          const msg = ({ closeToast, toastProps }) => (
            <div className='git-msg'>
              <div className='git-msg-title' style={{ color: 'green' }}>Your App is ready!</div>
              <div className='git-msg-body'>
                <div>Your application is now on GitHub ready to be cloned:</div>
                <div>
                  <code className='git-msg-code'>git clone {params.msg}</code>
                </div>
                <div>
                  <Button onClick={copy}>copy</Button>
                  <Button onClick={redirect}>show repo</Button>
                  <Button onClick={closeToast}>close</Button>
                </div>
              </div>
            </div>
          )
          toast.success(msg, { autoClose: false, closeOnClick: false });
        } else {
          const msg = ({ closeToast, toastProps }) => (
            <div className='git-msg'>
              <div className='git-msg-title' style={{ color: 'red' }}>Some mistake happend</div>
              <div className='git-msg-body'>
                <div>{params.msg}</div>
                <div style={{ marginTop: '20px' }}>
                  <Button onClick={closeToast}>close</Button>
                </div>
              </div>
            </div>
          )
          toast.error(msg, { autoClose: false, closeOnClick: false });
        }
      }
    }
  }, [complete, hash, dispatch, config])

  return null
}
