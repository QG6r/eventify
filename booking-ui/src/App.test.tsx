import React from 'react';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import App from './App';

describe('App (unauthenticated)', () => {
  beforeEach(() => {
    localStorage.clear();
  });

  test('shows the login form for a visitor', async () => {
    render(<App />);

    expect(await screen.findByText('Sign in to your account')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Sign in' })).toBeInTheDocument();
  });

  test('can switch from login to registration', async () => {
    render(<App />);

    userEvent.click(await screen.findByText("Don't have an account? Sign up"));

    expect(await screen.findByText('Create an account')).toBeInTheDocument();
  });
});
